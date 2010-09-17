package com.thoughtworks.go.reports;

import com.thoughtworks.go.*;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;

import java.util.HashMap;
import java.util.Map;

public class BuildBreaker {

    public static void main(String[] args) {
        HttpClientWrapper wrapper = new HttpClientWrapper("go03.thoughtworks.com", 8153);
        TalkToGo talkToGo = new TalkToGo(wrapper, false);
        talkToGo.visitAllStages(new BuildBreakers("pair02", "dev"));
    }

    private static class BuildBreakers implements StageVisitor {

        private final Map<String, Integer> userToCount = new HashMap<String, Integer>();
        private final String pipelineName;
        private final String stageName;

        public BuildBreakers(String pipelineName, String stageName) {
            this.pipelineName = pipelineName;
            this.stageName = stageName;
        }

        public void visitStage(Stage stage) {
            if (stage.hasFailed() && stage.getName().equalsIgnoreCase(stageName) && stage.getPipelineName().equalsIgnoreCase(pipelineName)) {
            }
        }

        public void visitPipeline(Pipeline pipeline) {
        }
    }
}
