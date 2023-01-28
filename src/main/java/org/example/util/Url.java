package org.example.util;

import org.example.App;

public class Url {

    public static String buildUrl(String path) {
        if (path.startsWith("/")) {
            return App.PROPERTIES.getHost() + path;
        }
        return App.PROPERTIES.getHost() + "/" + path;
    }
}
