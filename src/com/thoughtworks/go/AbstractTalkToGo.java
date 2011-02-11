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

    protected Stage stage(FeedEntry entry) {
        Stage stage = Stage.create(httpClient.get(HttpClientWrapper.scrub(entry.getResourceLink(), "/api/stages/")));
        stage.using(httpClient);
        return stage;
    }

    protected List<FeedEntry> stageFeedEntries() {
        String feedText = httpClient.get(feedUrl());
        FeedEntries feedEntries = FeedEntries.create(feedText);
        List<FeedEntry> elements = feedEntries.getEntries();
        while (infiniteCrawler && feedEntries.getNextLink() != null) {
            feedText = httpClient.get(feedUrl(), UrlUtil.parametersFrom(feedEntries.getNextLink()));
            feedEntries = FeedEntries.create(feedText);
            elements.addAll(feedEntries.getEntries());
        }
        return elements;
    }

    protected abstract String feedUrl();
    
    protected boolean matchesStage(String pipeline, String stage, FeedEntry entry) {
        return entry.matchesStage(pipeline, stage);
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
            if (!criteria.shouldContinueVisiting()) {
                return;
            }
        }
    }

    protected Stage findLatestStageFor(String pipeline, String stage) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesStage(pipeline, stage, entry)) {
                return stage(entry);
            }
        }
        throw new RuntimeException(String.format("Cannot find the stage [%s under %s]", stage, pipeline));
    }

    protected Pipeline findLatestPipeline(String name) {
        List<FeedEntry> entries = stageFeedEntries();
        for (FeedEntry entry : entries) {
            if (matchesPipeline(name, entry)) {
                Stage stage = stage(entry);
                return stage.using(httpClient).getPipeline();
            }
        }
        throw new RuntimeException(String.format("Cannot find the pipeline [%s]", name));
    }

    private boolean matchesPipeline(String pipelineName, FeedEntry entry) {
        return entry.getTitle().matches(String.format("^%s/.*?/.*?/\\d+", pipelineName));
    }

    public Pipeline latestPipeline() {
        return findLatestPipeline(pipelineName);
    }

    public Stage latestStage(String stageName) {
        return findLatestStageFor(pipelineName, stageName);
    }
}
