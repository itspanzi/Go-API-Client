package com.thoughtworks.go.util;

/**
 * @understands the environment in which Go-API-Client operates
 */
public class SystemEnvironment {

    public static String getCacheFolder() {
        String val = System.getenv("API_CLIENT_CACHE_FOLDER");
        return val == null ? "cache_folder" : val ;
    }

    public static boolean shouldUseCahce() {
        String val = System.getenv("API_CLIENT_CACHE_HTTP_RESPONSE");
        return val == null || Boolean.parseBoolean(val);
    }
}
