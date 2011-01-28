package com.thoughtworks.go.visitor.criteria;

import com.thoughtworks.go.domain.FeedEntry;

/**
 * @understands when a stage should be visited
 */
public interface VisitingCriteria {
    boolean shouldVisit(FeedEntry feedEntry);

    boolean shouldContinue();
}
