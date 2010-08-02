package com.thoughtworks.go;

/**
 * @understands when a stage entry is encountered
 */
public interface StageVisitor {
    void visitStage(Stage stage);

    void visitPipeline(Pipeline pipeline);
}
