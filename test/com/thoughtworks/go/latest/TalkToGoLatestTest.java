package com.thoughtworks.go.latest;

import com.thoughtworks.go.domain.FeedEntries;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.VisitingCriteria;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TalkToGoLatestTest {
    private HttpClientWrapper httpClientWrapper;
    private TalkToGoLatest talkToGo;

    @Before
    public void setup() throws IOException {
        httpClientWrapper = mock(HttpClientWrapper.class);
        talkToGo = new TalkToGoLatest("pipeline", httpClientWrapper, false);
        when(httpClientWrapper.get("/api/pipelines/pipeline/stages.xml")).thenReturn(file("2.4/feed.xml"));
    }

    @Test
    public void shouldReturnTheLatestStage() throws Exception {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-9.xml"));
        assertThat(talkToGo.latestStage("stage"), is(Stage.create(file("2.4/stage-9.xml"))));
    }

    @Test
    public void shouldReturnTheLatestPipeline() throws Exception {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("2.4/pipeline-9.xml"));
        assertThat(talkToGo.latestPipeline(), is(Pipeline.create(file("2.4/pipeline-9.xml"))));
    }

    @Test
    public void shouldCallBackForEveryEntryInTheLimitedFeed() throws Exception {
        Stage stage9 = Stage.create(file("2.4/stage-9.xml"));
        Pipeline pipeline9 = Pipeline.create(file("2.4/pipeline-9.xml"));

        Stage stage8 = Stage.create(file("2.4/stage-8.xml"));
        Pipeline pipeline8 = Pipeline.create(file("2.4/pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("2.4/pipeline-9.xml"));

        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("2.4/stage-8.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/8.xml")).thenReturn(file("2.4/pipeline-8.xml"));

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
        talkToGo = new TalkToGoLatest("pipeline", httpClientWrapper, true);

        when(httpClientWrapper.get("/api/pipelines/pipeline/stages.xml")).thenReturn(file("2.4/feed.xml"));
        stubWithParams("/api/pipelines/pipeline/stages.xml", "2.4/feed-2.xml", "before", 8);
        stubWithParams("/api/pipelines/pipeline/stages.xml", "2.4/feed-3.xml", "before", 6);
        stubVisiting();

        StageVisitor visitor = mock(StageVisitor.class);

        talkToGo.visitAllStages(visitor);

        verify(visitor).visitStage(Stage.create(file("2.4/stage-9.xml")));
        verify(visitor).visitStage(Stage.create(file("2.4/stage-8.xml")));
        verify(visitor).visitStage(Stage.create(file("2.4/stage-7.xml")));
        verify(visitor).visitStage(Stage.create(file("2.4/stage-6.xml")));
    }

    @Test
    public void shouldCallBackOnlyIfACriteriaMatches() throws Exception {
        talkToGo = new TalkToGoLatest("pipeline", httpClientWrapper, false);

        String feedXml = file("2.4/feed.xml");

        FeedEntries feedEntries = FeedEntries.create(feedXml);
        when(httpClientWrapper.get("/api/pipelines/pipeline/stages.xml")).thenReturn(feedXml);
        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("2.4/stage-10.xml"));

        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-11.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("2.4/pipeline-8.xml"));

        VisitingCriteria criteria = mock(VisitingCriteria.class);
        when(criteria.shouldVisit(feedEntries.getEntries().get(0))).thenReturn(true);
        when(criteria.shouldVisit(feedEntries.getEntries().get(1))).thenReturn(false);
        StageVisitor visitor = mock(StageVisitor.class);
        talkToGo.visitStages(visitor, criteria);

        verify(visitor).visitStage(Stage.create(file("2.4/stage-11.xml")));
        verify(visitor).visitPipeline(Pipeline.create(file("2.4/pipeline-8.xml")));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldCallEndCrawlingIfCriteriaSaysSo() throws Exception {
        talkToGo = new TalkToGoLatest("pipeline", httpClientWrapper, false);

        String feedXml = file("2.4/feed.xml");

        FeedEntries feedEntries = FeedEntries.create(feedXml);
        when(httpClientWrapper.get("/api/pipelines/pipeline/stages.xml")).thenReturn(feedXml);
        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("2.4/stage-10.xml"));

        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-11.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("2.4/pipeline-8.xml"));

        VisitingCriteria criteria = mock(VisitingCriteria.class);
        when(criteria.shouldVisit(feedEntries.getEntries().get(0))).thenReturn(true);
        when(criteria.shouldVisit(feedEntries.getEntries().get(1))).thenReturn(true);
        when(criteria.shouldContinueVisiting()).thenReturn(false);

        StageVisitor visitor = mock(StageVisitor.class);
        talkToGo.visitStages(visitor, criteria);

        verify(visitor).visitStage(Stage.create(file("2.4/stage-11.xml")));
        verify(visitor).visitPipeline(Pipeline.create(file("2.4/pipeline-8.xml")));
        verifyNoMoreInteractions(visitor);
    }

    private void stubVisiting() throws IOException {
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("2.4/stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("2.4/pipeline-9.xml"));

        when(httpClientWrapper.get("/api/stages/8.xml")).thenReturn(file("2.4/stage-8.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/8.xml")).thenReturn(file("2.4/pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/7.xml")).thenReturn(file("2.4/stage-7.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/7.xml")).thenReturn(file("2.4/pipeline-8.xml"));

        when(httpClientWrapper.get("/api/stages/6.xml")).thenReturn(file("2.4/stage-6.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/6.xml")).thenReturn(file("2.4/pipeline-8.xml"));
    }

    private void stubWithParams(String path, String resourceFile, String param, int value) throws IOException {
        Map<String, String> methodParams = paramMap(param, value);
        when(httpClientWrapper.get(path, methodParams)).thenReturn(file(resourceFile));
    }

    private Map<String, String> paramMap(String param, int value) {
        Map<String, String> methodParams = new HashMap<String, String>();
        methodParams.put(param, value + "");
        return methodParams;
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File("testdata/" + name));
    }
}
