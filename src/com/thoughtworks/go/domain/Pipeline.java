package com.thoughtworks.go.domain;

import com.thoughtworks.go.http.HttpClientWrapper;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.go.http.HttpClientWrapper.scrub;
import static com.thoughtworks.go.util.XmlUtil.*;

/**
 * @understands an instance of a Go Pipeline
 */
public class Pipeline {
    private final String name;
    private final int counter;
    private final String label;
    private final String scheduleTime;
    private final List<Material> materials;
    private final List<PipelineStage> pipelineStages;
    private final String approvedBy;
    private HttpClientWrapper httpClientWrapper;

    private Pipeline(String name, int counter, String label, String scheduleTime, List<Material> materials, List<PipelineStage> pipelineStages, String approvedBy) {
        this.name = name;
        this.counter = counter;
        this.label = label;
        this.scheduleTime = scheduleTime;
        this.materials = materials;
        this.pipelineStages = pipelineStages;
        this.approvedBy = approvedBy;
    }

    public static Pipeline create(String pipelineResource) {
        Document doc = parse(pipelineResource);
        String name = attrVal(singleNode(doc, "/pipeline"), "name");
        String counter = attrVal(singleNode(doc, "/pipeline"), "counter");
        String label = attrVal(singleNode(doc, "/pipeline"), "label");
        String scheduleTime = nodeText(doc, "//scheduleTime");
        String approvedBy = nodeText(doc, "//approvedBy");
        return new Pipeline(name, Integer.parseInt(counter), label, scheduleTime, materials(doc), stages(doc), approvedBy);
    }

    private static List<Material> materials(Document doc) {
        List<Material> materials = new ArrayList<Material>();
        for (Element material : nodes(doc, "//material")) {
            materials.add(Material.create(material.asXML()));
        }
        return materials;
    }

    private static List<PipelineStage> stages(Document doc) {
        List<PipelineStage> pipelineStages = new ArrayList<PipelineStage>();
        for (Element stageElement : nodes(doc, "//stage")) {
            pipelineStages.add(new PipelineStage(attrVal(stageElement, "href")));
        }
        return pipelineStages;
    }

    /**
     * Returns the user who triggered this pipeline or the string 'changes' which indicates that the pipeline was automatically
     * triggered by Go.
     *
     * @return the user name who triggered the pipeline or 'changes'.
     */
    public String getApprovedBy() {
        return approvedBy;
    }

    /**
     * Returns the list of materials that this pipeline instance was configured with.
     *
     * @return the list of all the materials this pipeline was configured with.
     */
    public List<Material> getMaterials() {
        return materials;
    }

    /**
     * Returns the name of this pipeline instance.
     *
     * @return the name of this pipeline instance
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the counter of this pipeline instance.
     *
     * @return the counter of this pipeline instance.
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Returns the label of this pipeline instance.
     *
     * @return the label of this pipeline instance.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the date in ISO8601 format when this pipeline was scheduled.
     *
     * @return the schedule date in ISO8601 format.
     */
    public String getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Returns all the stage instances which belong to this pipeline. This call hits the Go server to fetch all the stage
     * resources and return them as objects.
     *
     * This call is not cached since the stage resource could have changed i.e.
     * when this call is made a stage could still be building.
     *
     * @return the list of all stage instances in this pipeline
     */
    public List<Stage> getStages() {
        List<Stage> stages = new ArrayList<Stage>();
        for (PipelineStage pipelineStage : pipelineStages) {
            Stage stage = Stage.create(httpClientWrapper.get(scrub(pipelineStage.stageLink, "/api/stages")));
            stage.using(httpClientWrapper);
            stages.add(stage);
        }
        return stages;
    }

    public Pipeline using(HttpClientWrapper client) {
        this.httpClientWrapper = client;
        return this;
    }

    private static class PipelineStage {

        String stageLink;

        private PipelineStage(String stageLink) {
            this.stageLink = stageLink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PipelineStage that = (PipelineStage) o;

            if (stageLink != null ? !stageLink.equals(that.stageLink) : that.stageLink != null) return false;

            return true;
        }
        @Override
        public int hashCode() {
            return stageLink != null ? stageLink.hashCode() : 0;
        }

    }

    private void ensureClientIsSet() {
        if (httpClientWrapper == null) {
            throw new IllegalStateException("You can get jobs only when the http client is set. Try doing a using(HttpClient)");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pipeline pipeline = (Pipeline) o;

        if (counter != pipeline.counter) return false;
        if (approvedBy != null ? !approvedBy.equals(pipeline.approvedBy) : pipeline.approvedBy != null) return false;
        if (label != null ? !label.equals(pipeline.label) : pipeline.label != null) return false;
        if (materials != null ? !materials.equals(pipeline.materials) : pipeline.materials != null) return false;
        if (name != null ? !name.equals(pipeline.name) : pipeline.name != null) return false;
        if (pipelineStages != null ? !pipelineStages.equals(pipeline.pipelineStages) : pipeline.pipelineStages != null)
            return false;
        if (scheduleTime != null ? !scheduleTime.equals(pipeline.scheduleTime) : pipeline.scheduleTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + counter;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (scheduleTime != null ? scheduleTime.hashCode() : 0);
        result = 31 * result + (materials != null ? materials.hashCode() : 0);
        result = 31 * result + (pipelineStages != null ? pipelineStages.hashCode() : 0);
        result = 31 * result + (approvedBy != null ? approvedBy.hashCode() : 0);
        return result;
    }
}
