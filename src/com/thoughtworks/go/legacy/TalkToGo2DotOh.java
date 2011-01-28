package com.thoughtworks.go.legacy;

import com.thoughtworks.go.AbstractTalkToGo;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;

import java.util.List;

/**
 * @understands Talking to a Go 2.0 server using its APIs
 */
@SuppressWarnings({"unchecked"})
public class TalkToGo2DotOh extends AbstractTalkToGo {

    public TalkToGo2DotOh(HttpClientWrapper httpClient, boolean infiniteCrawler) {
        super(httpClient, infiniteCrawler);
    }

    @Override
    protected String feedUrl() {
        return "/api/feeds/stages.xml";
    }

    public Pipeline latestPipelineFor(String name) {
        return findLatestPipeline(name);
    }

    public Stage latestStageFor(String pipeline, String stage) {
        return findLatestStageFor(pipeline, stage);
    }

}
