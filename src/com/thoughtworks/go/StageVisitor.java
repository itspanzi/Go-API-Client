package com.thoughtworks.go;

import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;

/**
 * @understands when a stage entry is encountered
 */
public interface StageVisitor {
    void visitStage(Stage stage);

    void visitPipeline(Pipeline pipeline);
}
