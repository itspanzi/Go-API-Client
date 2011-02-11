package com.thoughtworks.go.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

import com.thoughtworks.go.util.SystemEnvironment;

/**
 * @understands communicating with the Go server using http
 */
public class HttpClientWrapper {
    private final String hostname;
    private final int port;
    private HttpMethodFactory factory;
    private HttpClient client;
    private Map<URI, Header> requestToEtag = new HashMap<URI, Header>();

    public HttpClientWrapper(String hostname, int port, String username, String password) {
        this(hostname, port, username, password, new HttpMethodFactory());
    }

    public HttpClientWrapper(String hostname, int port, String username, String password, HttpMethodFactory factory) {
        this.hostname = hostname;
        this.port = port;
        this.factory = factory;
        this.client = client(username, password);
    }

    public HttpClientWrapper(String hostname, int port) {
        this(hostname, port, null, null);
    }

    public String get(String path) {
        return get(path, null);
    }

    public String get(String path, Map<String, String> params) {
        try {
            HttpURL httpURL = new HttpURL(baseUrl() + path);
            HttpMethod getMethod = methodFor(httpURL, params);

            populateEtagIfRequired(getMethod, httpURL);

            int returnCode = client.executeMethod(getMethod);

            if (SystemEnvironment.shouldUseCahce() && returnCode == 304 && etagCacheFile(httpURL).exists()) {
                return FileUtils.readFileToString(responseCacheFile(httpURL));
            }

            if (isSuccessful(returnCode)) {
                String response = getMethod.getResponseBodyAsString();
                setupCacheIfRequired(httpURL, getMethod, response);
                return response;
            }
            throw new RuntimeException(String.format("The request to [%s] could not be completed. Response [%s] was returned with code [%s]", path, getMethod.getResponseBodyAsString(), returnCode));
        } catch (IOException e) {
            throw new RuntimeException("Connection pooped", e);
        }
    }

    private void setupCacheIfRequired(HttpURL httpURL, HttpMethod getMethod, String response) {
        if (SystemEnvironment.shouldUseCahce()) {
            saveEtag(getMethod, httpURL);
            cacheResponse(response, httpURL);
        }
    }

    private void cacheResponse(String response, HttpURL httpURL) {
        File file = responseCacheFile(httpURL);
        try {
            FileUtils.writeStringToFile(file, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpMethod methodFor(HttpURL httpURL, Map<String, String> params) throws URIException {
        HttpMethod getMethod = factory.create(HttpMethodFactory.GET);
        getMethod.setURI(httpURL);
        if (params != null) {
            getMethod.setParams(httpParams(params));
        }
        return getMethod;
    }

    private void populateEtagIfRequired(HttpMethod method, URI httpURL) {
        if (SystemEnvironment.shouldUseCahce() && etagCacheFile(httpURL).exists()) {
            try {
                method.addRequestHeader("Etag", FileUtils.readFileToString(etagCacheFile(httpURL)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveEtag(HttpMethod getMethod, URI httpURL) {
        Header header = getMethod.getResponseHeader("Etag");
        if (header != null) {
            try {
                FileUtils.writeStringToFile(etagCacheFile(httpURL), header.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File etagCacheFile(URI httpURL) {
        try {
            String fileName = new String(DigestUtils.md5(httpURL.getURI())) + "_etag";
            return new File(SystemEnvironment.getCacheFolder(), fileName);
        } catch (URIException e) {
            throw new RuntimeException(e);
        }
    }

    private File responseCacheFile(HttpURL httpURL) {
        try {
            String fileName = new String(DigestUtils.md5(httpURL.getURI()));
            return new File(SystemEnvironment.getCacheFolder(), fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSuccessful(int returnCode) {
        return returnCode >= 200 && returnCode < 300;
    }

    private HttpMethodParams httpParams(Map<String, String> params) {
        HttpMethodParams methodParams = new HttpMethodParams();
        for (String param : params.keySet()) {
            methodParams.setParameter(param,  params.get(param));
        }
        return methodParams;
    }

    private String baseUrl() {
        return String.format("http://%s:%d/go", hostname, port);
    }

    public static String scrub(String fullLink, String actualBegining) {
        return fullLink.substring(fullLink.indexOf(actualBegining));
    }

    private HttpClient client(String username, String password) {
        HttpClient httpClient = new HttpClient();
        if (username != null) {
            httpClient.getParams().setAuthenticationPreemptive(true);
            httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        return httpClient;
    }

    void setClient(HttpClient httpClient) {
        this.client = httpClient;
    }
}
