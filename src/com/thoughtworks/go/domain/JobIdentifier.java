package com.thoughtworks.go.domain;

/**
 * @understands identifying an instance of a job uniquely.
 */
public class JobIdentifier {

    private final StageIdentifier stageIdentifier;
    private final String jobName;

    public JobIdentifier(String pipelineName, int pipelineCounter, String stageName, int stageCounter, String jobName) {
        this.stageIdentifier = new StageIdentifier(pipelineName, pipelineCounter, stageName, stageCounter);
        this.jobName = jobName;
    }

    public String getPipelineName() {
        return stageIdentifier.getPipelineName();
    }

    public int getPipelineCounter() {
        return stageIdentifier.getPipelineCounter();
    }

    public String getStageName() {
        return stageIdentifier.getStageName();
    }

    public int getStageCounter() {
        return stageIdentifier.getStageCounter();
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobIdentifier that = (JobIdentifier) o;

        if (jobName != null ? !jobName.equals(that.jobName) : that.jobName != null) return false;
        if (stageIdentifier != null ? !stageIdentifier.equals(that.stageIdentifier) : that.stageIdentifier != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stageIdentifier != null ? stageIdentifier.hashCode() : 0;
        result = 31 * result + (jobName != null ? jobName.hashCode() : 0);
        return result;
    }
}
