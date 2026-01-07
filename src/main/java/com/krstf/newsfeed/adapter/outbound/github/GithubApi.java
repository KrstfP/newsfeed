package com.krstf.newsfeed.adapter.outbound.github;

import com.krstf.newsfeed.domain.models.Article;
import com.krstf.newsfeed.port.outbound.publish.PublishInGithub;

import com.github.slugify.Slugify;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class GithubApi implements PublishInGithub {

    private final Slugify slugify = Slugify.builder().build();
    private GHRepository repository;
    private final String repoCommitPath;

    public GithubApi(
            @Value("${github.token}") String token,
            @Value("${github.repo}") String repo,
            @Value("${github.path}") String path
    ) throws IOException {

        GitHub github = new GitHubBuilder()
                .withOAuthToken(token)
                .build();

        this.repository = github.getRepository(repo);
        this.repoCommitPath = path;
    }


    @Override
    public void publishAnalysis(Article article, String analysis) {
        String title = "%s-%s-%s";
        String slugifiedTitle = title.formatted(
                LocalDate.now().toString(),
                slugify.slugify(article.getTitle()),
                article.getId().toString().toLowerCase().substring(0, 8)
        );
        System.out.println(slugifiedTitle);

        try {
            String branchName = "auto/%s".formatted(article.getId().toString().toLowerCase());
            GHRef main = repository.getRef("heads/main");
            repository.createRef("refs/heads/" + branchName, main.getObject().getSha());

            repository.createContent()
                    .path("%s/%s.md".formatted(this.repoCommitPath, slugifiedTitle))
                    .message(article.getTitle())
                    .content(analysis)
                    .branch(branchName)
                    .commit();

            repository.createPullRequest(
                    "Merge %s into main".formatted(branchName),
                    branchName,
                    "main",
                    """
                      Merge article %s
                    """.formatted(article.getTitle())
            );

        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

    }
}
