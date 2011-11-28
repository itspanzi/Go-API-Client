package com.thoughtworks.go.visitor.criteria;

import com.thoughtworks.go.domain.FeedEntry;

/**
 * @understands visiting all the stage instances of a given stage in the feed.
 */
public class MatchingStageVisitor implements VisitingCriteria {
    private final String stageName;

    public MatchingStageVisitor(String stageName) {
        this.stageName = stageName;
    }

    public boolean shouldVisit(FeedEntry feedEntry) {
        return feedEntry.matchesStage(stageName);
    }

    public boolean shouldContinueVisiting() {
        return true;
    }
}
