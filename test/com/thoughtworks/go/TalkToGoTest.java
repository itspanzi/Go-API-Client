package com.thoughtworks.go;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TalkToGoTest {
    private HttpClientWrapper httpClientWrapper;
    private TalkToGo talkToGo;

    @Before
    public void setup() throws IOException {
        httpClientWrapper = mock(HttpClientWrapper.class);
        talkToGo = new TalkToGo(httpClientWrapper);
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
    public void shouldCallBackForEveryEntryInTheFeed() throws Exception {
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
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
