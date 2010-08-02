package com.thoughtworks.go;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.File;
import java.util.List;

import static junit.framework.Assert.fail;

public class StageTest {

    @Test
    public void shouldCreateAStage() throws Exception {
        Stage stage = Stage.create(file("stage-9.xml"));
        assertThat(stage.getName(), is("stage"));
        assertThat(stage.getCounter(), is(1));
        assertThat(stage.getPipelineName(), is("pipeline"));
        assertThat(stage.getPipelineCounter(), is(9));
        assertThat(stage.getPipelineLabel(), is("83"));
        assertThat(stage.getLastUpdated(), is("2010-07-30T22:00:15+05:30"));
        assertThat(stage.getResult(), is("Failed"));
        assertThat(stage.getState(), is("Completed"));
        assertThat(stage.getApprovedBy(), is("CruiseTimer"));
    }

    @Test
    public void shouldBombWhenNoHttpClientIsSet() throws Exception {
        Stage stage = Stage.create(file("stage-9.xml"));
        try {
            stage.getJobs();
            fail("Should have bombed");
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), is("You can get jobs only when the http client is set. Try doing a using(HttpClient)"));
        }
    }

    @Test
    public void shouldLazilyLoadJobs() throws Exception {
        Stage stage = Stage.create(file("stage-9.xml"));
        HttpClientWrapper wrapper = mock(HttpClientWrapper.class);
        when(wrapper.get("/api/jobs/1.xml")).thenReturn(file("job-1.xml"));
        when(wrapper.get("/api/jobs/2.xml")).thenReturn(file("job-2.xml"));
        List<Job> jobs = stage.using(wrapper).getJobs();
        assertThat(jobs.size(), is(2));
        assertThat(jobs.get(0), is(Job.create(file("job-1.xml"))));
        assertThat(jobs.get(1), is(Job.create(file("job-2.xml"))));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
