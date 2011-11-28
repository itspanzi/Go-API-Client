package com.thoughtworks.go.domain;

import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.util.DateUtil;
import com.thoughtworks.go.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.go.http.HttpClientWrapper.scrub;
import static com.thoughtworks.go.util.XmlUtil.*;

/**
 * @understands an instance of a Go stage
 */
public class Stage {
    private final String name;
    private final int counter;
    private final StagePipeline pipeline;
    private final Date lastUpdated;
    private final String result;
    private final StageState state;
    private final String approvedBy;
    private final List<StageJob> stageJobs;
    private HttpClientWrapper httpClientWrapper;

    private Stage(String name, int counter, StagePipeline pipeline, Date lastUpdated, String result, StageState state, String approvedBy, List<StageJob> stageJobs) {
        this.name = name;
        this.counter = counter;
        this.pipeline = pipeline;
        this.lastUpdated = lastUpdated;
        this.result = result;
        this.state = state;
        this.approvedBy = approvedBy;
        this.stageJobs = stageJobs;
    }

    public static Stage create(String resourceText) {
        Document doc = parse(resourceText);
        String name = attrVal(singleNode(doc, "/stage"), "name");
        String counter = attrVal(singleNode(doc, "/stage"), "counter");
        Element pipeline = singleNode(doc, "//pipeline");
        StagePipeline pip = new StagePipeline(attrVal(pipeline, "name"), attrVal(pipeline, "counter"), attrVal(pipeline, "label"), attrVal(pipeline, "href"));
        Date lastUpdated = DateUtil.toDate(nodeText(doc, "//updated"));
        String result = nodeText(doc, "//result");
        StageState state = StageState.valueOf(nodeText(doc, "//state"));
        String approvedBy = nodeText(doc, "//approvedBy");
        List<StageJob> stageJobs = stageJobs(doc);
        return new Stage(name, Integer.parseInt(counter), pip, lastUpdated, result, state, approvedBy, stageJobs);
    }

    private static List<StageJob> stageJobs(Document doc) {
        List<StageJob> stageJobs = new ArrayList<StageJob>();
        List<Element> jobs = XmlUtil.nodes(doc, "//job");
        for (Element stageJob : jobs) {
            stageJobs.add(new StageJob(attrVal(stageJob, "href")));
        }
        return stageJobs;
    }

    /**
     * Returns the url to the pipeline instance resource to which this stage instance belongs to.
     * <br/></br>
     * @return url to the pipeline instance
     */
    public String getPipelineUrl() {
        return pipeline.pipelineLink;
    }

    /**
     * Returns the name of this stage instance.
     *
     * @return the name of the stage.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the counter of this stage instance.
     *
     * @return the counter of the stage.
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Returns the name of the pipeline to which this stage instance belongs to.
     *
     * @return the name of the pipeline.
     */
    public String getPipelineName() {
        return pipeline.pipelineName;
    }

    /**
     * Returns the counter of the pipeline instance to which this stage instance belongs to.
     *
     * @return the counter of the pipeline.
     */
    public int getPipelineCounter() {
        return pipeline.pipelineCounter;
    }

    /**
     * Returns the label of the pipeline to which this stage instance belongs to.
     *
     * @return the label of the pipeline.
     */
    public String getPipelineLabel() {
        return pipeline.pipelineLabel;
    }

    /**
     * Returns the date time in ISO8601 format when the stage was updated the last time.
     *
     * @return the last updated time.
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Returns the result of the stage. Result can be one of "<b>Passed</b>", "<b>Failed</b>" or "<b>Cancelled</b>"
     *
     * @return the result of the stage.
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the state of the stage. State is "<b>Completed</b>", "<b>Building</b>", "<b>Unknown></b>" and "<b>Failing</b>".
     *
     * @return the state of the stage.
     */
    public StageState getState() {
        return state;
    }

    /**
     * Returns the name of the user who approved this stage. If this stage was automatically triggered by Go, it shows up
     * as "changes".
     *
     * @return the name of the user who approved the stage or "changes".
     */
    public String getApprovedBy() {
        return approvedBy;
    }

    public Stage using(HttpClientWrapper httpClientWrapper) {
        this.httpClientWrapper = httpClientWrapper;
        return this;
    }

    /**
     * Returns the list of all the jobs that belong to this stage. This call hits the Go server to fetch all the job
     * resources that belong to this stage instance.
     * <br/><br/>
     * This call does not cache the returned job instances and hence hits the Go server every time this method is called.
     * This is done since the job resource under the hood might have changed with new updates.
     *
     * @return the list of all the jobs that belong to this stage instance.
     */
    public List<Job> getJobs() {
        ensureClientIsSet();
        List<Job> jobs = new ArrayList<Job>();
        for (StageJob stageJob : this.stageJobs) {
            jobs.add(Job.create(httpClientWrapper.get(scrub(stageJob.jobLink, "/api/jobs"))));
        }
        return jobs;
    }

    private void ensureClientIsSet() {
        if (httpClientWrapper == null) {
            throw new IllegalStateException("You can get jobs only when the http client is set. Try doing a using(HttpClient)");
        }
    }

    /**
     * Returns the pipeline instance that this stage instance belongs to. Most of the information about a pipeline are
     * available on this object itself. Use this if you want to get access to the changeset etc.
     * <br/><br/>
     * This hits the Go server to get the pipeline instance.
     *
     * @return the pipeline instance to which this stage belongs to.
     */
    public Pipeline getPipeline() {
        ensureClientIsSet();
        return Pipeline.create(httpClientWrapper.get(scrub(pipeline.pipelineLink, "/api/pipelines")));
    }

    /**
     * A helper method that returns true if this stage instance has failed.
     *
     * @return true if the stage has failed.
     */
    public boolean hasFailed() {
        return state.equals("Completed") && result.equalsIgnoreCase("Failed");
    }

    /**
     * Return the stage locator i.e. a string of the format "<b>pipeline_name/pipeline_counter/stage_name/stage_counter</b>"
     * for this stage.
     *
     * @return the stage locator for this stage.
     */
    public String getStageLocator() {
        return String.format("%s/%s/%s/%s", getPipelineName(), getPipelineCounter(), getName(), getCounter());
    }

    private static class StagePipeline {

        String pipelineName;
        int pipelineCounter;
        String pipelineLabel;
        String pipelineLink;

        private StagePipeline(String pipelineName, String pipelineCounter, String pipelineLabel, String pipelineLink) {
            this.pipelineName = pipelineName;
            this.pipelineCounter = Integer.parseInt(pipelineCounter);
            this.pipelineLabel = pipelineLabel;
            this.pipelineLink = pipelineLink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StagePipeline that = (StagePipeline) o;

            if (pipelineCounter != that.pipelineCounter) return false;
            if (pipelineLabel != null ? !pipelineLabel.equals(that.pipelineLabel) : that.pipelineLabel != null)
                return false;
            if (pipelineLink != null ? !pipelineLink.equals(that.pipelineLink) : that.pipelineLink != null)
                return false;
            if (pipelineName != null ? !pipelineName.equals(that.pipelineName) : that.pipelineName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = pipelineName != null ? pipelineName.hashCode() : 0;
            result = 31 * result + pipelineCounter;
            result = 31 * result + (pipelineLabel != null ? pipelineLabel.hashCode() : 0);
            result = 31 * result + (pipelineLink != null ? pipelineLink.hashCode() : 0);
            return result;
        }
    }

    private static class StageJob {

        String jobLink;

        private StageJob(String jobLink) {
            this.jobLink = jobLink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StageJob stageJob = (StageJob) o;

            if (jobLink != null ? !jobLink.equals(stageJob.jobLink) : stageJob.jobLink != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return jobLink != null ? jobLink.hashCode() : 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stage stage = (Stage) o;

        if (counter != stage.counter) return false;
        if (approvedBy != null ? !approvedBy.equals(stage.approvedBy) : stage.approvedBy != null) return false;
        if (lastUpdated != null ? !lastUpdated.equals(stage.lastUpdated) : stage.lastUpdated != null) return false;
        if (name != null ? !name.equals(stage.name) : stage.name != null) return false;
        if (pipeline != null ? !pipeline.equals(stage.pipeline) : stage.pipeline != null) return false;
        if (result != null ? !result.equals(stage.result) : stage.result != null) return false;
        if (stageJobs != null ? !stageJobs.equals(stage.stageJobs) : stage.stageJobs != null) return false;
        if (state != null ? !state.equals(stage.state) : stage.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + counter;
        result = 31 * result + (pipeline != null ? pipeline.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
        result = 31 * result + (this.result != null ? this.result.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (approvedBy != null ? approvedBy.hashCode() : 0);
        result = 31 * result + (stageJobs != null ? stageJobs.hashCode() : 0);
        return result;
    }
}
