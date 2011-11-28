package com.thoughtworks.go.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @understands some common API needed to deal with URLs
 */
public class UrlUtil {

    /**
     * Given a url, returns a map name to value of the query parameters.
     *
     * @param url the url
     * @return the map of name to value of query parameters.
     */
    public static Map<String, String> parametersFrom(String url) {
        try {
            Map<String, String> httpMethodParams = new HashMap<String, String>();
            String queryString = new URI(url).getQuery();
            if (queryString == null) {
                return httpMethodParams;
            }
            addParameters(httpMethodParams, queryString);
            return httpMethodParams;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing url for parameters: " + url, e);
        }
    }

    private static void addParameters(Map<String, String> httpMethodParams, String queryString) {
        for (String param : queryString.split("&")) {
            StringTokenizer tokenizer = new StringTokenizer(param, "=");
            httpMethodParams.put(tokenizer.nextToken(), tokenizer.nextToken());
        }
    }

    /**
     * Returns the path given a full URL.
     *
     * @param url url
     * @return the path
     */
    public static String pathFrom(String url) {
        try {
            return new URI(url).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing url for parameters: " + url, e);
        }
    }
}
