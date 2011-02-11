package com.thoughtworks.go.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;

/**
 * @understands creating the right http method to talk to a server
 */
public class HttpMethodFactory {

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";

    public HttpMethod create(String method) {
        if (GET.equals(method)) {
            return new GetMethod();
        }
        if (POST.equals(method)) {
            return new PostMethod();
        }
        if (PUT.equals(method)) {
            return new PutMethod();
        }
        if (DELETE.equals(method)) {
            return new DeleteMethod();
        }
        throw new RuntimeException("Unknown method: " + method);
    }
}
