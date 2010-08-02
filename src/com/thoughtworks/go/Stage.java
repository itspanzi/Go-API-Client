package com.thoughtworks.go;

import com.thoughtworks.go.util.XmlUtil;
import static com.thoughtworks.go.util.XmlUtil.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;
import java.util.ArrayList;

/**
 * @understands an instace of a Go stage
 */
public class Stage {
    private final String name;
    private final int counter;
    private final StagePipeline pipeline;
    private final String lastUpdated;
    private final String result;
    private final String state;
    private final String approvedBy;
    private final List<StageJob> stageJobs;

    private Stage(String name, int counter, StagePipeline pipeline, String lastUpdated, String result, String state, String approvedBy, List<StageJob> stageJobs) {
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
        String lastUpdated = nodeText(doc, "//updated");
        String result = nodeText(doc, "//result");
        String state = nodeText(doc, "//state");
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

    public String getPipelineUrl() {
        return pipeline.pipelineLink;
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
