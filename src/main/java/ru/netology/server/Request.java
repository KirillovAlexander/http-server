package ru.netology.server;

import java.util.List;

public class Request {
    private String method;
    private String path;
    private String version;
    private List<String> headers;
    private String body;

    public Request() {
    }

    public Request(String method, String path, String version, List<String> headers, String body) {
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
            this.headers.add(header);
        }
    }

    public void addHeader(String header) {
        this.headers.add(header);
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
