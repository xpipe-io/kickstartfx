package io.xpipe.kickstart_fx.update;

import io.xpipe.kickstart_fx.issue.ErrorEventFactory;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.authorization.AuthorizationProvider;

import java.io.IOException;
import java.util.Optional;

public class AppDownloads {

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
        repository = github.getRepository("xpipe-io/xpipe");
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
