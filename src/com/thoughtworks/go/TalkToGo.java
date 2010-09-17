package com.thoughtworks.go;

import com.thoughtworks.go.domain.FeedEntries;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import static com.thoughtworks.go.http.HttpClientWrapper.scrub;
import com.thoughtworks.go.util.UrlUtil;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;

import java.util.List;

/**
 * @understands Talking to a Go server using its APIs
 */
@SuppressWarnings({"unchecked"})
public class TalkToGo {
    private final HttpClientWrapper httpClient;
    private final boolean infiniteCrawler;

    public TalkToGo(HttpClientWrapper httpClient, boolean infiniteCrawler) {
        this.httpClient = httpClient;
        this.infiniteCrawler = infiniteCrawler;
    }

    public Pipeline latestPipelineFor(String name) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesPipeline(name, entry)) {
                Stage stage = stage(entry);
                return stage.using(httpClient).getPipeline();
            }
        }
        throw new RuntimeException("Not yet implemented");
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

    private Stage stage(FeedEntry entry) {
        Stage stage = Stage.create(httpClient.get(scrub(entry.getResourceLink(), "/api/stages/")));
        stage.using(httpClient);
        return stage;
    }

    private List<FeedEntry> stageFeedEntries() {
        String feedText = httpClient.get("/api/feeds/stages.xml");
        FeedEntries feedEntries = FeedEntries.create(feedText);
        List<FeedEntry> elements = feedEntries.getEntries();
        while (infiniteCrawler && feedEntries.getNextLink() != null) {
            feedText = httpClient.get("/api/feeds/stages.xml", UrlUtil.parametersFrom(feedEntries.getNextLink()));
            feedEntries = FeedEntries.create(feedText);
            elements.addAll(feedEntries.getEntries());
        }
        return elements;
    }

    private boolean matchesStage(String pipeline, String stage, FeedEntry entry) {
        return match(entry, String.format("^%s/\\d+/%s/\\d+", pipeline, stage));
    }

    private boolean matchesPipeline(String pipelineName, FeedEntry entry) {
        return match(entry, String.format("^%s/.*?/.*?/\\d+", pipelineName));
    }

    private boolean match(FeedEntry entry, String regex) {
        return entry.getTitle().matches(regex);
    }

    public void visitAllStages(StageVisitor visitor) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            visit(visitor, entry);
        }
    }

    private void visit(StageVisitor visitor, FeedEntry entry) {
        try {
            Stage stage = stage(entry);
            visitor.visitStage(stage);
            visitor.visitPipeline(stage.getPipeline());
        } catch (Exception e) {
            //TODO: replace with logging
            System.out.println("Skipping entry because of an exception.\n" + entry + "\n");
            e.printStackTrace();
        }
    }

    public void visitStages(StageVisitor visitor, VisitingCriteria criteria) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (criteria.shouldVisit(entry)) {
                visit(visitor, entry);
            }
        }
    }
}
