package com.thoughtworks.go.http;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

import com.thoughtworks.go.util.SystemEnvironment;

public class HttpClientWrapperTest {

    @Before
    public void setup() {
        FileUtils.deleteQuietly(new File(SystemEnvironment.getCacheFolder()));
    }

    @Test
    public void testShouldSendTheEtagSentByTheServerForAGetRequest() {
        HttpMethodFactory mockFactory = mock(HttpMethodFactory.class);
        when(mockFactory.create(HttpMethodFactory.GET)).thenReturn(new MockGetMethod());

        HttpClientWrapper wrapper = new HttpClientWrapper("hostname", 8153, null, null, mockFactory);
        MockHttpClient mockHttpClient = new MockHttpClient();
        wrapper.setClient(mockHttpClient);

        wrapper.get("/go/api/stages");
        assertThat(mockHttpClient.requestEtag, is(nullValue()));

        wrapper.get("/go/api/stages");
        assertThat(mockHttpClient.requestEtag.getName(), is("If-None-Match"));
        assertThat(mockHttpClient.requestEtag.getValue(), is("123"));
    }

    @Test
    public void testShouldUseACachedResponseWhenTheServerReturnsA304() {
        HttpMethodFactory mockFactory = mock(HttpMethodFactory.class);
        when(mockFactory.create(HttpMethodFactory.GET)).thenReturn(new MockGetMethodThatReturnsValueOnlyOnce());

        HttpClientWrapper wrapper = new HttpClientWrapper("hostname", 8153, null, null, mockFactory);
        MockHttpClient mockHttpClient = new MockHttpClient();
        wrapper.setClient(mockHttpClient);

        String response = wrapper.get("/go/api/stages");
        assertThat(response, is("some response"));
        response = wrapper.get("/go/api/stages");
        assertThat(response, is("some response"));
    }

    @Test
    public void testShouldOverwriteTheCacheWhenTheServerReturnsA200() {
        HttpMethodFactory mockFactory = mock(HttpMethodFactory.class);
        when(mockFactory.create(HttpMethodFactory.GET)).thenReturn(new MockGetMethod());

        HttpClientWrapper wrapper = new HttpClientWrapper("hostname", 8153, null, null, mockFactory);
        wrapper.setClient(new MockHttpClientThatKeepsModifying());

        String response = wrapper.get("/go/api/stages");
        assertThat(response, is("first time"));
        response = wrapper.get("/go/api/stages");
        assertThat(response, is("other time"));
    }

    private static class MockHttpClient extends HttpClient {

        Header requestEtag;

        @Override
        public int executeMethod(HttpMethod httpMethod) throws IOException {
            this.requestEtag = httpMethod.getRequestHeader("If-None-Match");
            if (requestEtag == null) {
                ((MockGetMethod) httpMethod).addHeader("Etag", "123");
                return 200;
            }
            return 304;
        }
    }

    private static class MockHttpClientThatKeepsModifying extends MockHttpClient {

        Header requestEtag;

        @Override
        public int executeMethod(HttpMethod httpMethod) throws IOException {
            this.requestEtag = httpMethod.getRequestHeader("If-None-Match");
            if (requestEtag == null) {
                ((MockGetMethod) httpMethod).addHeader("Etag", "123");
            }
            return 200;
        }
    }

    private static class MockGetMethod extends GetMethod {

        boolean firstTime = true;

        public void addHeader(String name, String value) {
            getResponseHeaderGroup().addHeader(new Header(name, value));
        }

        @Override
        public InputStream getResponseBodyAsStream() throws IOException {
            if (firstTime) {
                firstTime = false;
                return new ByteArrayInputStream("first time".getBytes());
            }
            return new ByteArrayInputStream("other time".getBytes());
        }

        @Override
        public String getResponseBodyAsString() throws IOException {
            if (firstTime) {
                firstTime = false;
                return "first time";
            }
            return "other time";
        }
    }

    private static class MockGetMethodThatReturnsValueOnlyOnce extends MockGetMethod {

        boolean answered;

        @Override
        public String getResponseBodyAsString() throws IOException {
            if (answered) {
                throw new RuntimeException("Should not have come here as the response should have been cached");
            }
            return "some response";
        }

        @Override
        public InputStream getResponseBodyAsStream() throws IOException {
            if (answered) {
                throw new RuntimeException("Should not have come here as the response should have been cached");
            }
            return new ByteArrayInputStream("some response".getBytes());
        }
    }
}
