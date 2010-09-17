package com.thoughtworks.go.visitorcriteria;

import com.thoughtworks.go.domain.FeedEntry;

/**
 * @understands when a stage should be visited
 */
public interface VisitingCriteria {
    boolean shouldVisit(FeedEntry feedEntry);
}
