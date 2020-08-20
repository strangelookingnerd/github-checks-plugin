package io.jenkins.plugins.checks.github;

import java.util.Optional;

import org.jenkinsci.plugins.github_branch_source.GitHubAppCredentials;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;

@SuppressWarnings({"PMD.CloseResource", "rawtypes"})// no need to close mocked PrintStream
class GitHubChecksPublisherFactoryTest {
    private static final String URL = "URL";

    @Test
    void shouldCreateGitHubChecksPublisherFromRun() {
        Run run = mock(Run.class);
        Job job = mock(Job.class);
        GitHubSCMSource source = mock(GitHubSCMSource.class);
        GitHubAppCredentials credentials = mock(GitHubAppCredentials.class);
        SCMFacade scmFacade = mock(SCMFacade.class);

        when(run.getParent()).thenReturn(job);
        when(scmFacade.findGitHubSCMSource(job)).thenReturn(Optional.of(source));
        when(source.getCredentialsId()).thenReturn("credentials id");
        when(scmFacade.findGitHubAppCredentials(job, "credentials id")).thenReturn(Optional.of(credentials));

        GitHubChecksPublisherFactory factory = new GitHubChecksPublisherFactory(scmFacade);
        assertThat(factory.createPublisher(run, URL, TaskListener.NULL))
                .isPresent()
                .containsInstanceOf(GitHubChecksPublisher.class);
    }

    @Test
    void shouldReturnGitHubChecksPublisherFromJob() {
        Job<?, ?> job = mock(Job.class);
        GitHubSCMSource source = mock(GitHubSCMSource.class);
        SCMFacade scmFacade = mock(SCMFacade.class);

        when(scmFacade.findGitHubSCMSource(job)).thenReturn(Optional.of(source));
        when(source.getCredentialsId()).thenReturn("credentials id");
        when(scmFacade.findGitHubAppCredentials(job, "credentials id"))
                .thenReturn(Optional.empty());

        GitHubChecksPublisherFactory factory = new GitHubChecksPublisherFactory(scmFacade);
        assertThat(factory.createPublisher(job, URL, TaskListener.NULL))
                .isNotPresent();
    }

    @Test
    void shouldReturnEmptyWhenNoGitHubSCMSourceIsConfigured() {
        Run run = mock(Run.class);

        GitHubChecksPublisherFactory factory = new GitHubChecksPublisherFactory();
        assertThat(factory.createPublisher(run, URL, TaskListener.NULL))
                .isNotPresent();
    }

    @Test
    void shouldReturnEmptyWhenNoCredentialsIsConfigured() {
        Run run = mock(Run.class);
        Job job = mock(Job.class);
        GitHubSCMSource source = mock(GitHubSCMSource.class);
        SCMFacade scmFacade = mock(SCMFacade.class);

        when(run.getParent()).thenReturn(job);
        when(scmFacade.findGitHubSCMSource(run.getParent())).thenReturn(Optional.of(source));
        when(source.getCredentialsId()).thenReturn(null);

        GitHubChecksPublisherFactory factory = new GitHubChecksPublisherFactory(scmFacade);
        assertThat(factory.createPublisher(job, URL, TaskListener.NULL))
                .isNotPresent();
    }

    @Test
    void shouldReturnEmptyWhenNoGitHubAppCredentialsIsConfigured() {
        Run run = mock(Run.class);
        Job job = mock(Job.class);
        GitHubSCMSource source = mock(GitHubSCMSource.class);
        SCMFacade scmFacade = mock(SCMFacade.class);

        when(run.getParent()).thenReturn(job);
        when(scmFacade.findGitHubSCMSource(run.getParent())).thenReturn(Optional.of(source));
        when(source.getCredentialsId()).thenReturn("credentials id");
        when(scmFacade.findGitHubAppCredentials(run.getParent(), "credentials id"))
                .thenReturn(Optional.empty());

        GitHubChecksPublisherFactory factory = new GitHubChecksPublisherFactory(scmFacade);
        assertThat(factory.createPublisher(job, URL, TaskListener.NULL))
                .isNotPresent();
    }
}