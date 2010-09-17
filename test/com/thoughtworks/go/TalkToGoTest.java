package com.thoughtworks.go;

import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.FeedEntries;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.visitorcriteria.StageVisitor;
import com.thoughtworks.go.visitorcriteria.VisitingCriteria;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TalkToGoTest {
    private HttpClientWrapper httpClientWrapper;
    private TalkToGo talkToGo;

    @Before
    public void setup() throws IOException {
        httpClientWrapper = mock(HttpClientWrapper.class);
        talkToGo = new TalkToGo(httpClientWrapper, false);
        when(httpClientWrapper.get("/api/feeds/stages.xml")).thenReturn(file("feed.xml"));
    }

    @Test
    public void shouldReturnTheLatestStage() throws Exception {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        assertThat(talkToGo.latestStageFor("pipeline", "stage"), is(Stage.create(file("stage-9.xml"))));
    }

    @Test
    public void shouldReturnTheLatestPipeline() throws Exception {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("pipeline-9.xml"));
        assertThat(talkToGo.latestPipelineFor("pipeline"), is(Pipeline.create(file("pipeline-9.xml"))));
    }

    @Test
    public void shouldCallBackForEveryEntryInTheLimitedFeed() throws Exception {
        Stage stage9 = Stage.create(file("stage-9.xml"));
        Pipeline pipeline9 = Pipeline.create(file("pipeline-9.xml"));

        Stage stage8 = Stage.create(file("stage-8.xml"));
        Pipeline pipeline8 = Pipeline.create(file("pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("pipeline-9.xml"));

        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("stage-8.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/8.xml")).thenReturn(file("pipeline-8.xml"));

        StageVisitor visitor = mock(StageVisitor.class);
        talkToGo.visitAllStages(visitor);

        verify(visitor).visitStage(stage9);
        verify(visitor).visitPipeline(pipeline9);
        verify(visitor).visitStage(stage8);
        verify(visitor).visitPipeline(pipeline8);
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldCallBackForEveryEntryInTheFeedUntilTheEnd() throws Exception {
        talkToGo = new TalkToGo(httpClientWrapper, true);

        when(httpClientWrapper.get("/api/feeds/stages.xml")).thenReturn(file("feed.xml"));
        stubWithParams("/api/feeds/stages.xml", "feed-2.xml", "before", 8);
        stubWithParams("/api/feeds/stages.xml", "feed-3.xml", "before", 6);
        stubVisiting();

        StageVisitor visitor = mock(StageVisitor.class);

        talkToGo.visitAllStages(visitor);

        verify(visitor).visitStage(Stage.create(file("stage-9.xml")));
        verify(visitor).visitStage(Stage.create(file("stage-8.xml")));
        verify(visitor).visitStage(Stage.create(file("stage-7.xml")));
        verify(visitor).visitStage(Stage.create(file("stage-6.xml")));
    }

    @Test
    public void shouldCallBackOnlyIfACriteriaMatches() throws Exception {
        talkToGo = new TalkToGo(httpClientWrapper, false);

        String feedXml = file("testdata/criteria-feed.xml");

        FeedEntries feedEntries = FeedEntries.create(feedXml);
        when(httpClientWrapper.get("/api/feeds/stages.xml")).thenReturn(feedXml);
        when(httpClientWrapper.get("/api/stages/10.xml")).thenReturn(file("testdata/stage-10.xml"));

        when(httpClientWrapper.get("/api/stages/11.xml")).thenReturn(file("testdata/stage-11.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("testdata/pipeline-8.xml"));

        VisitingCriteria criteria = mock(VisitingCriteria.class);
        when(criteria.shouldVisit(feedEntries.getEntries().get(0))).thenReturn(true);
        when(criteria.shouldVisit(feedEntries.getEntries().get(1))).thenReturn(false);
        StageVisitor visitor = mock(StageVisitor.class);
        talkToGo.visitStages(visitor, criteria);

        verify(visitor).visitStage(Stage.create(file("testdata/stage-11.xml")));
        verify(visitor).visitPipeline(Pipeline.create(file("testdata/pipeline-8.xml")));
        verifyNoMoreInteractions(visitor);
    }

    private void stubVisiting() throws IOException {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("pipeline-9.xml"));

        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("stage-8.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/8.xml")).thenReturn(file("pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/7.xml")).thenReturn(file("stage-7.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/7.xml")).thenReturn(file("pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/6.xml")).thenReturn(file("stage-6.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/6.xml")).thenReturn(file("pipeline-8.xml"));
    }

    private void stubWithParams(String path, String resourceFile, String param, int value) throws IOException {
        Map<String, String> methodParams = new HashMap<String, String>();
        methodParams.put(param, value + "");
        when(httpClientWrapper.get(path, methodParams)).thenReturn(file(resourceFile));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
