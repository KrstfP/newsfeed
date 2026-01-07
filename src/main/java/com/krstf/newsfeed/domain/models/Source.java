package com.krstf.newsfeed.domain.models;

import java.net.URI;

public class Source {
    URI rssFeedUrl;
    String name;
    String description;

    public Source(URI rssFeedUrl, String name, String description) {
        this.rssFeedUrl = rssFeedUrl;
        this.name = name;
        this.description = description;
    }

    public URI getRssFeedUrl() {
        return rssFeedUrl;
    }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Source{" +
                "rssFeedUrl=" + rssFeedUrl +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
