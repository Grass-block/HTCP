package me.gb2022.htcp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import me.gb2022.htcp.handler.HTTPProxyHandler;
import me.gb2022.htcp.handler.TCPProxyHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpListenerInstance {
    private final HttpServer server;
    ILogger logger = LogManager.getLogger("Listener");

    public HttpListenerInstance(String serverAddress, JsonArray array) {

        try {
            String host = serverAddress.split(":")[0];
            int port = Integer.parseInt(serverAddress.split(":")[1]);
            this.server = HttpServer.create(new InetSocketAddress(host, port), 114514);
            this.server.start();
            this.logger.info("started server instance on %s.".formatted(serverAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (JsonElement element : array) {
            JsonObject handlerElement = element.getAsJsonObject();
            String type = handlerElement.get("type").getAsString();
            String local = handlerElement.get("local").getAsString();
            String remote = handlerElement.get("remote").getAsString();

            if (type.equals("direct")) {
                this.server.createContext(local, new HTTPProxyHandler(local, remote));
                this.logger.info("added direct context: %s -> %s".formatted(local, remote));
                continue;
            }
            String wrapper = handlerElement.get("wrapper").getAsString();
            this.server.createContext(local, new TCPProxyHandler(local, remote, wrapper));
            this.logger.info("added wrapped(%s) context: %s -> %s".formatted(wrapper, local, remote));
        }
    }

    public void stop() {
        this.server.stop(0);
    }
}
