package com.thoughtworks.go.latest;

import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.AbstractTalkToGo;

import java.io.File;

/**
 * @understands Talking to the latest stable version of the Go server using its APIs
 */
public class TalkToGoLatest extends AbstractTalkToGo {

    public TalkToGoLatest(String pipelineName, HttpClientWrapper httpClient, boolean infiniteCrawler) {
        super(pipelineName, httpClient, infiniteCrawler);
    }

    protected String feedUrl() {
        return String.format("/api/pipelines/%s/stages.xml", pipelineName);
    }

    public void enhance(Stage stage, FeedEntry entry) {
    }

    public void fetchArtifact(JobIdentifier jobIdentifier, String artifactLocation, File destinationDirectory) {
    }
}