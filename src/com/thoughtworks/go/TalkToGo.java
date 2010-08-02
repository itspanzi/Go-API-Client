package com.thoughtworks.go;

import com.thoughtworks.go.util.XmlUtil;
import org.dom4j.Element;

import java.util.List;

/**
 * @understands Talking to a Go server using its APIs
 */
@SuppressWarnings({"unchecked"})
public class TalkToGo {
    private final HttpClientWrapper httpClient;

    public TalkToGo(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }

    public Pipeline latestPipelineFor(String name) {
        List<Element> entries = stageFeedEntries();
        for (Element entry : entries) {
            if (matchesPipeline(name, entry)) {
                Stage stage = stage(entry);
                return stage.using(httpClient).getPipeline();
            }
        }
        throw new RuntimeException("Not yet implemented");
    }

    public Stage latestStageFor(String pipeline, String stage) {
        List<Element> entries = stageFeedEntries();
        for (Element entry : entries) {
            if (matchesStage(pipeline, stage, entry)) {
                return stage(entry);
            }
        }
        throw new RuntimeException(String.format("Cannot find the stage [%s under %s]", stage, pipeline));
    }

    private Stage stage(Element entry) {
        return Stage.create(httpClient.get(HttpClientWrapper.scrub(stageResource(entry), "/api/stages/")));
    }

    private List<Element> stageFeedEntries() {
        String feed = httpClient.get("/api/feeds/stages.xml");
        return (List<Element>) XmlUtil.parse(feed).selectNodes("//a:entry");
    }

    private String stageResource(Element entry) {
        return ((Element) entry.selectSingleNode(".//a:link")).attribute("href").getValue();
    }

    private boolean matchesStage(String pipeline, String stage, Element entry) {
        return match(entry, String.format("^%s/\\d+/%s/\\d+", pipeline, stage));
    }

    private boolean matchesPipeline(String pipelineName, Element entry) {
        return match(entry, String.format("^%s/.*?/.*?/\\d+", pipelineName));
    }

    private boolean match(Element entry, String regex) {
        return entry.selectSingleNode(".//a:title").getText().matches(regex);
    }

    public void visitAllStages(StageVisitor visitor) {
        List<Element> entries = stageFeedEntries();
        for (Element entry : entries) {
            Stage stage = stage(entry);
            visitor.visitStage(stage);
            visitor.visitPipeline(stage.using(httpClient).getPipeline());
        }
    }
}
