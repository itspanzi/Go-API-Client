package com.thoughtworks.go.domain;

/**
 * @understands identifying an instance of a stage uniquely.
 */
public class StageIdentifier {

    private final String pipelineName;
    private final int pipelineCounter;
    private final String stageName;
    private final int stageCounter;

    public StageIdentifier(String pipelineName, int pipelineCounter, String stageName, int stageCounter) {
        this.pipelineName = pipelineName;
        this.pipelineCounter = pipelineCounter;
        this.stageName = stageName;
        this.stageCounter = stageCounter;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public int getPipelineCounter() {
        return pipelineCounter;
    }

    public String getStageName() {
        return stageName;
    }

    public int getStageCounter() {
        return stageCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StageIdentifier that = (StageIdentifier) o;

        if (pipelineCounter != that.pipelineCounter) return false;
        if (stageCounter != that.stageCounter) return false;
        if (pipelineName != null ? !pipelineName.equals(that.pipelineName) : that.pipelineName != null) return false;
        if (stageName != null ? !stageName.equals(that.stageName) : that.stageName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pipelineName != null ? pipelineName.hashCode() : 0;
        result = 31 * result + pipelineCounter;
        result = 31 * result + (stageName != null ? stageName.hashCode() : 0);
        result = 31 * result + stageCounter;
        return result;
    }
}
