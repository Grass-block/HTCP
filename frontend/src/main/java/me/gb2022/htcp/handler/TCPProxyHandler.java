package me.gb2022.htcp.handler;

import com.sun.net.httpserver.HttpExchange;
import me.gb2022.htcp.remote.HttpResponseData;
import me.gb2022.htcp.remote.RemoteInstanceManager;

public final class TCPProxyHandler extends ProxyHandler {
    private final String wrapper;

    public TCPProxyHandler(String localOrigin, String remoteOrigin, String wrapper) {
        super(localOrigin, remoteOrigin);
        this.wrapper = wrapper;
    }

    @Override
    public void handleASAPRequest(String path, HttpExchange context) throws Exception {
        HttpResponseData data = RemoteInstanceManager
                .getClientInstance(this.wrapper)
                .sendRequest(context.getRequestMethod(), path)
                .get();
        byte[] b = fixRequestOrigin(data.getData());
        context.sendResponseHeaders(data.getCode(), b.length);
        context.getResponseBody().write(b);
    }
}
