package com.thoughtworks.go.visitor;

import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;

/**
 * @understands when a stage entry is encountered
 */
public interface StageVisitor {
    /**
     * The stage object given as a call back. This is constructed with the stage resource XML. Jobs and Pipeline resource
     * may not be fetched yet. Any processing inside the call back may fetch new resources from the server.
     *
     * @param stage The stage object given as a call back from the crawler.
     */
    void visitStage(Stage stage);

    /**
     * The pipeline instance object associated with the given stage instance. This can be used to get information about
     * change sets, revisions, trigger information etc.
     * @param pipeline The pipeline object given as a call back from the crawler.
     */
    void visitPipeline(Pipeline pipeline);
}
