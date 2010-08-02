package com.thoughtworks.go;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TalkToGoTest {

    @Test
    public void shouldReturnTheLatestStage() throws Exception {
        HttpClientWrapper httpClientWrapper = mock(HttpClientWrapper.class);
        TalkToGo talkToGo = new TalkToGo(httpClientWrapper);
        when(httpClientWrapper.get("/api/feeds/stages.xml")).thenReturn(file("feed.xml"));
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        assertThat(talkToGo.latestStageFor("pipeline", "stage"), is(Stage.create(file("stage-9.xml"))));
    }

    @Test
    public void shouldReturnTheLatestPipeline() throws Exception {
        HttpClientWrapper httpClientWrapper = mock(HttpClientWrapper.class);
        TalkToGo talkToGo = new TalkToGo(httpClientWrapper);
        when(httpClientWrapper.get("/api/feeds/stages.xml")).thenReturn(file("feed.xml"));
        when(httpClientWrapper.get("/api/stages/9.xml")).thenReturn(file("stage-9.xml"));
        when(httpClientWrapper.get("/api/pipelines/pipeline/9.xml")).thenReturn(file("pipeline-9.xml"));
        assertThat(talkToGo.latestPipelineFor("pipeline"), is(Pipeline.create(file("pipeline-9.xml"))));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
