package ru.netology.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Request {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private String body;

    public Request() {
        this.headers = new ConcurrentHashMap<>();
    }

    public Request(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public void setRequestLine(String requestLine) {
        final String[] parts = requestLine.split(" ");
        this.method = parts[0];
        this.path = parts[1];
        this.version = parts[3];
    }

    public void setHeaders(String headers) {
        final String[] parts = headers.split("\n");
        for (String header: parts
        ) {
            String[] headerParts = header.split(":");
            if (headerParts.length == 2) {
                this.headers.put(headerParts[0], headerParts[1]);
            }
        }
    }

    public boolean addHeader(String header) {
        String[] headerParts = header.split(":");
        if (headerParts.length == 2) {
            this.headers.put(headerParts[0], headerParts[1]);
            return true;
        } else {
            return false;
        }
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getFirstPath() {
        String[] parts = path.split("/");
        if (parts.length == 0) return "";
        return "/" + parts[0];
    }

    public String getFullPath() {
        return path;
    }

}
