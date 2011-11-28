package com.thoughtworks.go.domain;

import static com.thoughtworks.go.util.XmlUtil.*;

import com.thoughtworks.go.util.DateUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * The name of the property that gives the date time when the job was scheduled
     */
    public static final String SCHEDULED_TIMESTAMP = "cruise_timestamp_01_scheduled";

    /**
     * The name of the property that gives the date time when the job was assigned to an agent
     */
    public static final String ASSIGNED_TIMESTAMP = "cruise_timestamp_02_assigned";

    /**
     * The name of the property that gives the date time when the job started preparing i.e. updating code for all the materials.
     */
    public static final String PREPARING_TIMESTAMP = "cruise_timestamp_03_preparing";

    /**
     * The name of the property that gives the date time when the job started building the tasks.
     */
    public static final String BUILDING_TIMESTAMP = "cruise_timestamp_04_building";

    /**
     * The name of the property that gives the date time when the job finished building all the tasks and started uploading artifacts.
     */
    public static final String COMPLETING_TIMESTAMP = "cruise_timestamp_05_completing";

    /**
     * The name of the property that gives the date time when the job was completed.
     */
    public static final String COMPLETED_TIMESTAMP = "cruise_timestamp_06_completed";

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

        String agentUUID = attrVal(singleNode(doc, "//agent"), "uuid");
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

    /**
     * Returns the name of the job instance.
     *
     * @return the name of the job instance
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the pipeline to which this job belonged to.
     *
     * @return the name of the pipeline.
     */
    public String getPipelineName() {
        return jobPipeline.name;
    }

    /**
     * Returns the pipeline counter of the pipeline instance to which this job belongs to.
     *
     * @return the pipeline counter.
     */
    public int getPipelineCounter() {
        return jobPipeline.counter;
    }

    /**
     * Returns the pipeline label of the pipeline instance to which this job belongs to.
     *
     * @return The pipeline label.
     */
    public String getPipelineLabel() {
        return jobPipeline.label;
    }

    /**
     * Returns the name of the stage to which this job belonged to.
     *
     * @return the name of the stage.
     */
    public String getStageName() {
        return jobStage.name;
    }

    /**
     * Returns the stage counter of the stage instance to which this job belongs to.
     *
     * @return the stage counter.
     */
    public int getStageCounter() {
        return jobStage.counter;
    }

    /**
     * Returns the state of the job. State can be one of: Completed, Rescheduled or Unknown.
     *
     * @return the state of the job.
     */
    public String getState() {
        return state;
    }

    /**
     * Returns the result of the job. Result can be one of: Passed, Failed, Cancelled or Unknown.
     *
     * @return The result of the job.
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the list of all the properties that this job has generated.
     *
     * @return the list of all the properties.
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Returns the list of all the resources that the job had been configured with when it was scheduled.
     *
     * @return The list of all the resources.
     */
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * Returns the list of all the environment variables that the job had been configured with when it was scheduled.
     * This list has all variables along with their actual values after all the overriding rules were applied.
     *
     * @return the list of all the environment variables.
     */
    public List<EnvVariable> getEnvVariables() {
        return envVariables;
    }

    /**
     * Returns the UUID of the agent on which this job was run. A UUID uniquely identifies an agent.
     *
     * @return the uuid of the agent.
     */
    public String getUUID() {
        return agentUUID;
    }

    /**
     * Returns the time that the job spent on the agent i.e. right from 'Preparing' to 'Completed'. This value is useful
     * to figure out things like what is the time that the job waited for an agent to become free, what is the time that an agent
     * is occupied for by a job etc.
     * <br/><br/>
     * If the job was cancelled, the time is returned as '0'. This is not exactly correct since the agent might have started building.
     * This is something that might get fixed later.
     * <br/><br/>
     * @return the time taken by the job on the agent in seconds. 0 if the job is cancelled.
     */
    public long timeSpentOnAgent() {
        if (result.equals("Cancelled") || state.equals("Cancelled")) {
            return 0;
        }
        long assigned = time(property(ASSIGNED_TIMESTAMP));
        long completed = time(property(COMPLETED_TIMESTAMP));
        return inSeconds(assigned, completed);
    }

    private long inSeconds(long assigned, long completed) {
        return (completed - assigned) / 1000;
    }

    private long time(Property property) {
        return DateUtil.toDateInMillis(property.value);
    }

    private Property property(String propertyName) {
        for (Property property : properties) {
            if (property.name.equals(propertyName)) {
                return property;
            }
        }
        throw new RuntimeException(String.format("Property '%s' not found", propertyName));
    }

    /**
     * Returns the value of a property with the given name. Throws an exception if the property with the given name is
     * not found.
     *
     * @param propertyName the name of the property.
     *
     * @return value of the property.
     */
    public String getProperty(String propertyName) {
        return property(propertyName).value;
    }

    /**
     * Returns the job identifier for this job. This is the unique identifier of a job that is used to encapsulate the
     * identifying of a job.
     *
     * @return the job identifier for the given job.
     */
    public JobIdentifier getJobIdentifier() {
        return new JobIdentifier(getPipelineName(), getPipelineCounter(), getStageName(), getStageCounter(), getName());
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

    /**
     * Understands what a job property is. All the transition times of a job are available as properties.
     */
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
