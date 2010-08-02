package com.thoughtworks.go;

import org.hamcrest.Matcher;
import org.dom4j.Document;
import org.dom4j.Element;
import com.thoughtworks.go.util.XmlUtil;
import static com.thoughtworks.go.util.XmlUtil.*;
import static com.thoughtworks.go.util.XmlUtil.nodes;

import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

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

    private Pipeline(String name, int counter, String label, String scheduleTime, List<Material> materials, List<PipelineStage> pipelineStages) {
        this.name = name;
        this.counter = counter;
        this.label = label;
        this.scheduleTime = scheduleTime;
        this.materials = materials;
        this.pipelineStages = pipelineStages;
    }

    public static Pipeline create(String pipelineResource) {
        Document doc = parse(pipelineResource);
        String name = attrVal(singleNode(doc, "/pipeline"), "name");
        String counter = attrVal(singleNode(doc, "/pipeline"), "counter");
        String label = attrVal(singleNode(doc, "/pipeline"), "label");
        String scheduleTime = nodeText(doc, "//scheduleTime");
        return new Pipeline(name, Integer.parseInt(counter), label, scheduleTime, materials(doc), stages(doc));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pipeline pipeline = (Pipeline) o;

        if (counter != pipeline.counter) return false;
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
        return result;
    }
}
