package io.abc_def.kickstart_fx.update;

import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.authorization.AuthorizationProvider;

import java.io.IOException;
import java.util.Optional;

public class AppDownloads {

    // TODO: Put your own repo URL here
    // For demo purposes, the xpipe updates are shown
    private static final String GITHUB_REPOSITORY = "xpipe-io/xpipe";

    private static GHRepository repository;

    @SuppressWarnings("deprecation")
    private static GHRepository getRepository() throws IOException {
        if (repository != null) {
            return repository;
        }

        var github = new GitHubBuilder()
                .withRateLimitHandler(RateLimitHandler.FAIL)
                .withAuthorizationProvider(AuthorizationProvider.ANONYMOUS)
                .build();
        repository = github.getRepository(GITHUB_REPOSITORY);
        return repository;
    }

    public static Optional<GHRelease> getMarkedLatestRelease() throws IOException {
        try {
            var repo = getRepository();
            return Optional.ofNullable(repo.getLatestRelease());
        } catch (IOException e) {
            throw ErrorEventFactory.expected(e);
        }
    }
}
