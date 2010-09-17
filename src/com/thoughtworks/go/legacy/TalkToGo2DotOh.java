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
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesPipeline(name, entry)) {
                Stage stage = stage(entry);
                return stage.using(httpClient).getPipeline();
            }
        }
        throw new RuntimeException(String.format("Cannot find the pipeline [%s]", name));
    }

    public Stage latestStageFor(String pipeline, String stage) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesStage(pipeline, stage, entry)) {
                return stage(entry);
            }
        }
        throw new RuntimeException(String.format("Cannot find the stage [%s under %s]", stage, pipeline));
    }

    private boolean matchesPipeline(String pipelineName, FeedEntry entry) {
        return entry.getTitle().matches(String.format("^%s/.*?/.*?/\\d+", pipelineName));
    }
}
