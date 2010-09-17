package com.thoughtworks.go.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Map;

public class UrlUtilTest {

    @Test
    public void shouldReturnTheParameters() throws Exception {
        Map<String, String> params = UrlUtil.parametersFrom("http://foo.bar?second=pavan&first=1");
        assertThat(params.get("first"), is("1"));
        assertThat(params.get("second"), is("pavan"));
    }

    @Test
    public void shouldReturnEmptyParameters() throws Exception {
        Map<String, String> params = UrlUtil.parametersFrom("http://foo.bar");
        assertThat(params.containsKey("first"), is(false));
        assertThat(params.containsKey("second"), is(false));
    }

    @Test
    public void shouldReturnSingleParameter() throws Exception {
        Map<String, String> params = UrlUtil.parametersFrom("http://foo.bar?second=pavan");
        assertThat(params.containsKey("first"), is(false));
        assertThat(params.get("second"), is("pavan"));
    }

    @Test
    public void shouldReturnThePathWhenThereIsNoPath() throws Exception {
        assertThat(UrlUtil.pathFrom("http://foo.bar?second=pavan"), is(""));
    }

    @Test
    public void shouldReturnThePath() throws Exception {
        assertThat(UrlUtil.pathFrom("http://foo.bar/baz?second=pavan"), is("/baz"));
    }

    @Test
    public void shouldReturnThePathWithoutParameters() throws Exception {
        assertThat(UrlUtil.pathFrom("http://foo.bar/baz/quux"), is("/baz/quux"));
    }
}
