package com.thoughtworks.go;

import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;

/**
 * @understands Talking to Go scoped to a given pipeline agnostic of the version 
 */
public interface TalkToGo {
    void visitAllStages(StageVisitor visitor);

    void visitStages(StageVisitor visitor, VisitingCriteria criteria);

    Pipeline latestPipeline();

    Stage latestStage(String stageName);
}
