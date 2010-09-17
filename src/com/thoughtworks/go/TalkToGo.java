package com.thoughtworks.go;

import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;

/**
 * @understands Talking to Go agnostic of the version
 */
public interface TalkToGo {
    void visitAllStages(StageVisitor visitor);

    void visitStages(StageVisitor visitor, VisitingCriteria criteria);
}
