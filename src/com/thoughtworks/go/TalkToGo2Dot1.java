package com.thoughtworks.go;

import com.thoughtworks.go.http.HttpClientWrapper;

/**
 * @understands Talking to a Go 2.0 server using its APIs 
 */
public class TalkToGo2Dot1 extends AbstractTalkToGo {
    private final String pipelineName;

    public TalkToGo2Dot1(String pipelineName, HttpClientWrapper httpClient, boolean infiniteCrawler) {
        super(httpClient, infiniteCrawler);
        this.pipelineName = pipelineName;
    }

    protected String feedUrl() {
        return String.format("/api/pipelines/%s/stages.xml", pipelineName);
    }
}
