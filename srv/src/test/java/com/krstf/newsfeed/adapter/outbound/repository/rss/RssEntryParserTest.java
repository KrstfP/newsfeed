package com.krstf.newsfeed.adapter.outbound.repository.rss;

import com.krstf.newsfeed.domain.models.RssFeedSource;
import com.krstf.newsfeed.domain.models.RssItem;
import com.rometools.rome.feed.synd.*;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RssEntryParserTest {

    private final RssEntryParser parser = new RssEntryParser();

    private RssFeedSource anySource() {
        return new RssFeedSource(URI.create("http://example.com/feed"), "Source", "desc");
    }

    private SyndEntry entryWithContents(String html) {
        SyndContentImpl content = new SyndContentImpl();
        content.setValue(html);
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle("titre");
        entry.setLink("http://example.com/article");
        entry.setContents(List.of(content));
        return entry;
    }

    private SyndEntry entryWithDescription(String html) {
        SyndContentImpl description = new SyndContentImpl();
        description.setValue(html);
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle("titre");
        entry.setLink("http://example.com/article");
        entry.setDescription(description);
        return entry;
    }

    private SyndEntry emptyEntry() {
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle("titre");
        entry.setLink("http://example.com/article");
        return entry;
    }

    // --- extractContent ---

    @Test
    void extractContent_withContents_returnsContentsValue() {
        SyndEntry entry = entryWithContents("<p>contenu principal</p>");
        assertEquals("<p>contenu principal</p>", parser.extractContent(entry));
    }

    @Test
    void extractContent_withDescriptionOnly_returnsDescription() {
        SyndEntry entry = entryWithDescription("<p>description</p>");
        assertEquals("<p>description</p>", parser.extractContent(entry));
    }

    @Test
    void extractContent_withNoContent_returnsEmptyString() {
        assertEquals("", parser.extractContent(emptyEntry()));
    }

    @Test
    void extractContent_contentsHasPriorityOverDescription() {
        SyndContentImpl content = new SyndContentImpl();
        content.setValue("contents");
        SyndContentImpl description = new SyndContentImpl();
        description.setValue("description");
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setContents(List.of(content));
        entry.setDescription(description);
        assertEquals("contents", parser.extractContent(entry));
    }

    // --- extractPublishedDate ---

    @Test
    void extractPublishedDate_withPublishedDate_returnsIt() {
        Date published = new Date(1_000_000L);
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setPublishedDate(published);
        assertEquals(published, parser.extractPublishedDate(entry));
    }

    @Test
    void extractPublishedDate_withUpdatedDateOnly_returnsUpdatedDate() {
        Date updated = new Date(2_000_000L);
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setUpdatedDate(updated);
        assertEquals(updated, parser.extractPublishedDate(entry));
    }

    @Test
    void extractPublishedDate_publishedDateHasPriorityOverUpdatedDate() {
        Date published = new Date(1_000_000L);
        Date updated = new Date(2_000_000L);
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setPublishedDate(published);
        entry.setUpdatedDate(updated);
        assertEquals(published, parser.extractPublishedDate(entry));
    }

    @Test
    void extractPublishedDate_withNoDate_returnsNonNull() {
        assertNotNull(parser.extractPublishedDate(new SyndEntryImpl()));
    }

    // --- extractCategories ---

    @Test
    void extractCategories_withCategories_returnsNames() {
        SyndCategoryImpl cat1 = new SyndCategoryImpl();
        cat1.setName("politique");
        SyndCategoryImpl cat2 = new SyndCategoryImpl();
        cat2.setName("défense");
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setCategories(List.of(cat1, cat2));
        assertEquals(List.of("politique", "défense"), parser.extractCategories(entry));
    }

    @Test
    void extractCategories_withNoCategories_returnsEmptyList() {
        assertTrue(parser.extractCategories(new SyndEntryImpl()).isEmpty());
    }

    // --- toArticle ---

    @Test
    void toArticle_stripsHtmlFromContent() {
        RssItem item = parser.toArticle(entryWithContents("<p>texte <b>en gras</b></p>"), anySource());
        assertEquals("texte en gras", item.getContent());
    }

    @Test
    void toArticle_mapsSourceFields() {
        RssFeedSource source = anySource();
        RssItem item = parser.toArticle(emptyEntry(), source);
        assertEquals(source.getId(), item.getSourceId());
        assertEquals(source.getName(), item.getSourceName());
    }
}
