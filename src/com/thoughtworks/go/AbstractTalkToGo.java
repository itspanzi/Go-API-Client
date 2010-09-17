package com.thoughtworks.go;

import com.thoughtworks.go.domain.FeedEntries;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.util.UrlUtil;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;

import java.util.List;

/**
 * ADD_UNDERSTANDS_BLOCK
 */
public abstract class AbstractTalkToGo implements TalkToGo {
    protected final HttpClientWrapper httpClient;
    protected final boolean infiniteCrawler;

    public AbstractTalkToGo(HttpClientWrapper httpClient, boolean infiniteCrawler) {
        this.httpClient = httpClient;
        this.infiniteCrawler = infiniteCrawler;
    }

    protected Stage stage(FeedEntry entry) {
        Stage stage = Stage.create(httpClient.get(com.thoughtworks.go.http.HttpClientWrapper.scrub(entry.getResourceLink(), "/api/stages/")));
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
        }
    }
}
