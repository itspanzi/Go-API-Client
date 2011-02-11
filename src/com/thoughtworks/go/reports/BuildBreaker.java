package com.thoughtworks.go.reports;

import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.two_dot_oh.TalkToGo2DotOh;
import com.thoughtworks.go.visitor.StageVisitor;

import java.util.HashMap;
import java.util.Map;

public class BuildBreaker {

    public static void main(String[] args) {
        HttpClientWrapper wrapper = new HttpClientWrapper("go03.thoughtworks.com", 8153);
        TalkToGo2DotOh talkToGo = new TalkToGo2DotOh("pair02", wrapper, false);
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
