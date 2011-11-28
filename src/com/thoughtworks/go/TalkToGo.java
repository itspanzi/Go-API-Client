package com.thoughtworks.go;

import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;

import java.io.File;
import java.util.List;

/**
 * @understands Talking to Go scoped to a given pipeline agnostic of the version.
 */
public interface TalkToGo {
    /**
     * For every feed entry that is fetched, the corresponding stage resource is fetched and given as a call back to the visitor.
     * This is ideal when you want to just get a call back on every single stage in the history.
     *
     * Go Feeds API is paginated. A single page contains a maximum of 25 feed entries. The implementation of this interface can
     * choose to crawl the feeds anyway it sees fit.
     *
     * This API does 'm + 25 * (m -1) + n' number of requests to the Go server where: 'm' is the number of pages of feed that
     * are crawled and 'n' is the number of entries in the last feed page.
     *
     * For example: If you want to construct a graph for the stage duration, you would want to use this.
     *
     * @param visitor Call back for every stage object in the feed.
     */
    void visitAllStages(StageVisitor visitor);

    /**
     * Just like the visitAllStages, but instead of visiting every stage in the feed, it visits only stages that match a passed in criteria.
     * The criteria also needs to return if the feed crawling should continue. So, the criteria controls both which stage to visit
     * and if crawling should continue.
     *
     * For example: If you want to construct the stage duration graph only for passed stages run in the last 3 months, you can use this.
     *
     * @param visitor Call back for every stage object in the feed.
     * @param criteria Criteria that says whether to visit a stage or not and whether to continue visiting or not.
     */
    void visitStages(StageVisitor visitor, VisitingCriteria criteria);

    /**
     * Gets the latest pipeline instance for this pipeline
     *
     * @return The latest pipeline instance.
     */
    Pipeline latestPipeline();

    /**
     * Returns the latest stage instance with the given name
     *
     * @param stageName The name of the stage that you want an instance for
     * @return the latest stage instance with the given name
     */
    Stage latestStage(String stageName);

    /**
     * Returns the raw feed entries. This does 'm' request to the Go server where 'm' is the number of pages crawled.
     * Feed entries have minimal information about stages. Use this if all you want is information about stages like status,
     * result, locators etc.
     *
     * @return a list of the feed entries.
     */
    List<FeedEntry> stageFeedEntries();

    void fetchArtifact(JobIdentifier jobIdentifier, String artifactLocation, File destinationDirectory);
}
