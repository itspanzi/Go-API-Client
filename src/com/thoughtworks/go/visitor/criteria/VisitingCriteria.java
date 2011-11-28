package com.thoughtworks.go.visitor.criteria;

import com.thoughtworks.go.domain.FeedEntry;

/**
 * @understands when a stage should be visited
 */
public interface VisitingCriteria {
    /**
     * Should a given feed entry be visited. If yes, a request is sent to the Go server to construct the stage object
     * and a visitor call back is issued.
     *
     * @param feedEntry A raw stage feed entry
     * @return Should the given feed entry be visited.
     */
    boolean shouldVisit(FeedEntry feedEntry);

    /**
     * Should the crawling of feeds continue? If yes, the next feed entry is processed. Otherwise the call back ends.
     *
     * @return Should the visiting be continued
     */
    boolean shouldContinueVisiting();
}
