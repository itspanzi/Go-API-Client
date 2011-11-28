package com.thoughtworks.go.domain;

import com.thoughtworks.go.http.HttpClientWrapper;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;

import com.thoughtworks.go.domain.Pipeline;

public class PipelineTest {

    @Test
    public void shouldReturnWhoTriggerThePipeline() throws Exception {
        Pipeline pipeline = Pipeline.create(file("testdata/2.4/pipeline-9.xml"));
        assertThat(pipeline.getApprovedBy(), is("CruiseTimer"));
    }

    @Test
    public void shouldReturnTheListOfStagesForThisPipeline() throws Exception {
        HttpClientWrapper wrapper = mock(HttpClientWrapper.class);
        Pipeline pipeline = Pipeline.create(file("testdata/2.4/pipeline-9.xml")).using(wrapper);

        when(wrapper.get("/api/stages/14138.xml")).thenReturn(file("testdata/2.4/stage-9.xml"));

        assertThat(pipeline.getStages(), is(Arrays.asList(Stage.create(file("testdata/2.4/stage-9.xml")))));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
