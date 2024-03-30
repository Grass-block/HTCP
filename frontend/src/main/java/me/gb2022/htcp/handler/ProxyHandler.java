package me.gb2022.htcp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public abstract class ProxyHandler implements HttpHandler {
    private final ILogger logger = LogManager.getLogger("Handler");
    private final String localOrigin;
    private final String remoteOrigin;

    public ProxyHandler(String localOrigin, String remoteOrigin) {
        this.localOrigin = localOrigin;
        this.remoteOrigin = remoteOrigin;
    }

    @Override
    public void handle(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();

        String path = uri.getPath().replaceFirst(this.localOrigin, "");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        String targetUrl = "http://" + this.remoteOrigin + path;
        try {
            this.handleASAPRequest(targetUrl, exchange);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    byte[] fixRequestOrigin(byte[] data) {
        String s = new String(data, StandardCharsets.UTF_8);
        if (s.contains("<head>")) {
            return s.replace("<head>", "<head><base href=\""+this.localOrigin + "/\" />").getBytes(StandardCharsets.UTF_8);
        } else {
            return data;
        }
    }

    public abstract void handleASAPRequest(String path, HttpExchange context) throws Exception;
}
