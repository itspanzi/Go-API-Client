package com.thoughtworks.go.visitor.criteria;

import com.thoughtworks.go.domain.FeedEntry;

/**
 * @understands to matching a feed entry if it is for the given Stage
 */
public class MatchingStageVisitor implements VisitingCriteria {
    private final String stageName;

    public MatchingStageVisitor(String stageName) {
        this.stageName = stageName;
    }

    public boolean shouldVisit(FeedEntry feedEntry) {
        return feedEntry.matchesStage(stageName);
    }

    public boolean shouldContinue() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
