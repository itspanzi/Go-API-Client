package com.thoughtworks.go.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.Map;

/**
 * @understands communicating with the Go server using http
 */
public class HttpClientWrapper {
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    public HttpClientWrapper(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public HttpClientWrapper(String hostname, int port) {
        this(hostname, port, null, null);
    }

    public String get(String path) {
        return get(path, null);
    }

    public String get(String path, Map<String, String> params) {
        try {
            GetMethod getMethod = new GetMethod(baseUrl() + path);
            if (params != null) {
                getMethod.setParams(httpParams(params));
            }
            int returnCode = client().executeMethod(getMethod);
            if (returnCode >= 200 && returnCode <= 200) {
                return getMethod.getResponseBodyAsString();
            }
            throw new RuntimeException(String.format("The request to [%s] could not be completed. Response [%s] was returned with code [%s]", path, getMethod.getResponseBodyAsString(), returnCode));
        } catch (IOException e) {
            throw new RuntimeException("Connection pooped", e);
        }
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

    private HttpClient client() {
        HttpClient httpClient = new HttpClient();
        if (username != null) {
            httpClient.getParams().setAuthenticationPreemptive(true);
            httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        return httpClient;
    }
}
