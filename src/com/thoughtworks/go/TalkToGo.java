package com.thoughtworks.go;

import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.util.UrlUtil;
import com.thoughtworks.go.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;

/**
 * @understands Talking to a Go server using its APIs
 */
@SuppressWarnings({"unchecked"})
public class TalkToGo {
    private final HttpClientWrapper httpClient;
    private final boolean infiniteCrawler;

    public TalkToGo(HttpClientWrapper httpClient, boolean infiniteCrawler) {
        this.httpClient = httpClient;
        this.infiniteCrawler = infiniteCrawler;
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
        Stage stage = Stage.create(httpClient.get(HttpClientWrapper.scrub(stageResource(entry), "/api/stages/")));
        stage.using(httpClient);
        return stage;
    }

    private List<Element> stageFeedEntries() {
        Document feed = XmlUtil.parse(httpClient.get("/api/feeds/stages.xml"));
        List<Element> elements = (List<Element>) feed.selectNodes("//a:entry");
        while (infiniteCrawler && XmlUtil.singleNode(feed, "//a:link[@rel='next']") != null) {
            Element next = XmlUtil.singleNode(feed, "//a:link[@rel='next']");
            feed = XmlUtil.parse(httpClient.get("/api/feeds/stages.xml", UrlUtil.parametersFrom(XmlUtil.attrVal(next, "href"))));
            elements.addAll((List<Element>) feed.selectNodes("//a:entry"));
        }
        return elements;
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
            try {
                Stage stage = stage(entry);
                visitor.visitStage(stage);
                visitor.visitPipeline(stage.getPipeline());
            } catch (Exception e) {
                //TODO: replace with logging
                System.out.println("Skipping entry because of an exception.\n" + entry.asXML() + "\n");
                e.printStackTrace();
            }
        }
    }


}
