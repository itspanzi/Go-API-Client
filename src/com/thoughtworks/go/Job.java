package com.thoughtworks.go;

import static com.thoughtworks.go.util.XmlUtil.*;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * @understands a Go job instance.
 */
public class Job {
    private final String name;
    private final JobPipeline jobPipeline;
    private final JobStage jobStage;
    private final String state;
    private final String result;
    private final String agentUUID;
    private final List<Property> properties;
    private final List<Resource> resources;
    private final List<EnvVariable> envVariables;

    private Job(String name, JobPipeline jobPipeline, JobStage jobStage, String state, String result, String agentUUID, List<Property> properties, List<Resource> resources, List<EnvVariable> envVariables) {
        this.name = name;
        this.jobPipeline = jobPipeline;
        this.jobStage = jobStage;
        this.state = state;
        this.result = result;
        this.agentUUID = agentUUID;
        this.properties = properties;
        this.resources = resources;
        this.envVariables = envVariables;
    }

    public static Job create(String resource) {
        Document doc = parse(resource);
        String name = attrVal(singleNode(doc, "/job"), "name");

        Element pipeline = singleNode(doc, "//pipeline");
        JobPipeline jobPipeline = new JobPipeline(attrVal(pipeline, "name"), Integer.parseInt(attrVal(pipeline, "counter")), attrVal(pipeline, "label"));

        Element stage = singleNode(doc, "//stage");
        JobStage jobStage = new JobStage(attrVal(stage, "name"), Integer.parseInt(attrVal(stage, "counter")), attrVal(stage, "href"));

        String state = nodeText(doc, "//state");
        String result = nodeText(doc, "//result");

        String agentUUID = nodeText(doc, "//agent");
        return new Job(name, jobPipeline, jobStage, state, result, agentUUID, properties(doc), resources(doc), envVars(doc));
    }

    private static List<EnvVariable> envVars(Document doc) {
        List<EnvVariable> envVariables = new ArrayList<EnvVariable>();
        for (Element element : nodes(doc, "//variable")) {
            envVariables.add(new EnvVariable(attrVal(element, "name"), element.getText()));
        }
        return envVariables;
    }

    private static List<Resource> resources(Document doc) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Element element : nodes(doc, "//resource")) {
            resources.add(new Resource(element.getText()));
        }
        return resources;
    }

    private static List<Property> properties(Document doc) {
        List<Property> properties = new ArrayList<Property>();
        for (Element element : nodes(doc, "//property")) {
            properties.add(new Property(attrVal(element, "name"), element.getText()));
        }
        return properties;
    }

    public String getName() {
        return name;
    }

    public String getPipelineName() {
        return jobPipeline.name;
    }

    public int getPipelineCounter() {
        return jobPipeline.counter;
    }

    public String getPipelineLabel() {
        return jobPipeline.label;
    }

    public String getStageName() {
        return jobStage.name;
    }

    public int getStageCounter() {
        return jobStage.counter;
    }

    public String getState() {
        return state;
    }

    public String getResult() {
        return result;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<EnvVariable> getEnvVariables() {
        return envVariables;
    }

    private static final class JobStage {
        private final String name;
        private final int counter;
        private final String link;

        public JobStage(String name, int counter, String link) {
            this.name = name;
            this.counter = counter;
            this.link = link;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JobStage jobStage = (JobStage) o;

            if (counter != jobStage.counter) return false;
            if (link != null ? !link.equals(jobStage.link) : jobStage.link != null) return false;
            if (name != null ? !name.equals(jobStage.name) : jobStage.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + counter;
            result = 31 * result + (link != null ? link.hashCode() : 0);
            return result;
        }
    }

    private static class JobPipeline {
        private final String name;
        private final int counter;
        private final String label;

        public JobPipeline(String name, int counter, String label) {
            this.name = name;
            this.counter = counter;
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JobPipeline that = (JobPipeline) o;

            if (counter != that.counter) return false;
            if (label != null ? !label.equals(that.label) : that.label != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + counter;
            result = 31 * result + (label != null ? label.hashCode() : 0);
            return result;
        }
    }

    public static class Property {

        private String name;
        private String value;

        public Property(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Property property = (Property) o;

            if (name != null ? !name.equals(property.name) : property.name != null)
                return false;
            if (value != null ? !value.equals(property.value) : property.value != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static class EnvVariable {

        private String name;
        private String value;

        public EnvVariable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnvVariable that = (EnvVariable) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static class Resource {

        private String name;

        public Resource(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Resource resource = (Resource) o;

            if (name != null ? !name.equals(resource.name) : resource.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (agentUUID != null ? !agentUUID.equals(job.agentUUID) : job.agentUUID != null) return false;
        if (jobPipeline != null ? !jobPipeline.equals(job.jobPipeline) : job.jobPipeline != null) return false;
        if (jobStage != null ? !jobStage.equals(job.jobStage) : job.jobStage != null) return false;
        if (name != null ? !name.equals(job.name) : job.name != null) return false;
        if (properties != null ? !properties.equals(job.properties) : job.properties != null) return false;
        if (resources != null ? !resources.equals(job.resources) : job.resources != null) return false;
        if (result != null ? !result.equals(job.result) : job.result != null) return false;
        if (state != null ? !state.equals(job.state) : job.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = name != null ? name.hashCode() : 0;
        result1 = 31 * result1 + (jobPipeline != null ? jobPipeline.hashCode() : 0);
        result1 = 31 * result1 + (jobStage != null ? jobStage.hashCode() : 0);
        result1 = 31 * result1 + (state != null ? state.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (agentUUID != null ? agentUUID.hashCode() : 0);
        result1 = 31 * result1 + (properties != null ? properties.hashCode() : 0);
        result1 = 31 * result1 + (resources != null ? resources.hashCode() : 0);
        return result1;
    }
}
