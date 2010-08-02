package com.thoughtworks.go;

/**
 * ADD_UNDERSTANDS_BLOCK
 */
public class HttpClientWrapper {

    public HttpClientWrapper(String hostname, int port) {
    }

    public String get(String path) {
        throw new RuntimeException("Not yet implemented");
    }

    static String scrub(String fullLink, String actualBegining) {
        return fullLink.substring(fullLink.indexOf(actualBegining));
    }
}
