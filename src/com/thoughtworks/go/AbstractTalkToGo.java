package com.thoughtworks.go;

import com.thoughtworks.go.domain.FeedEntries;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.util.UrlUtil;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;

import java.util.List;

/**
 * @understands the commonality across versions
 */
public abstract class AbstractTalkToGo implements TalkToGo {
    protected final HttpClientWrapper httpClient;
    protected final boolean infiniteCrawler;
    protected String pipelineName;

    public AbstractTalkToGo(String pipelineName, HttpClientWrapper httpClient, boolean infiniteCrawler) {
        this.httpClient = httpClient;
        this.infiniteCrawler = infiniteCrawler;
        this.pipelineName = pipelineName;
    }

    public void visitAllStages(StageVisitor visitor) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            visit(visitor, entry);
        }
    }

    public void visitStages(StageVisitor visitor, VisitingCriteria criteria) {
        while (true) {
            FeedEntries feedEntries = nextEntries();
            if (visitEntries(feedEntries.getEntries(), visitor, criteria) || !infiniteCrawler || feedEntries.getNextLink() == null) {
                break;
            }
        }
    }

    private boolean visitEntries(List<FeedEntry> entries, StageVisitor visitor, VisitingCriteria criteria) {
        for (FeedEntry entry : entries) {
            if (criteria.shouldVisit(entry)) {
                visit(visitor, entry);
            }
            if (!criteria.shouldContinueVisiting()) {
                return true;
            }
        }
        return false;
    }

    public Stage latestStage(String stageName) {
        return findLatestStageFor(pipelineName, stageName);
    }

    public Pipeline latestPipeline() {
        return findLatestPipeline(pipelineName);
    }

    protected abstract String feedUrl();

    protected abstract void enhance(Stage stage, FeedEntry entry);

    private Stage stage(FeedEntry entry) {
        Stage stage = Stage.create(httpClient.get(HttpClientWrapper.scrub(entry.getResourceLink(), "/api/stages/")));
        enhance(stage, entry);
        stage.using(httpClient);
        return stage;
    }

    private List<FeedEntry> stageFeedEntries() {
        FeedEntries feedEntries = nextEntries();
        List<FeedEntry> elements = feedEntries.getEntries();
        String feedText;
        while (infiniteCrawler && feedEntries.getNextLink() != null) {
            feedText = httpClient.get(feedUrl(), UrlUtil.parametersFrom(feedEntries.getNextLink()));
            feedEntries = FeedEntries.create(feedText);
            elements.addAll(feedEntries.getEntries());
        }
        return elements;
    }

    private FeedEntries nextEntries() {
        String feedText = httpClient.get(feedUrl());
        return FeedEntries.create(feedText);
    }

    private boolean matchesStage(String pipeline, String stage, FeedEntry entry) {
        return entry.matchesStage(pipeline, stage);
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

    private Stage findLatestStageFor(String pipeline, String stage) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesStage(pipeline, stage, entry)) {
                return stage(entry);
            }
        }
        throw new RuntimeException(String.format("Cannot find the stage [%s under %s]", stage, pipeline));
    }

    private Pipeline findLatestPipeline(String name) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (entry.matchesPipeline(name)) {
                Stage stage = stage(entry);
                return stage.using(httpClient).getPipeline();
            }
        }
        throw new RuntimeException(String.format("Cannot find the pipeline [%s]", name));
    }
}
