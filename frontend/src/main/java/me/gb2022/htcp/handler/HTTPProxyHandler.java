package me.gb2022.htcp.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPProxyHandler extends ProxyHandler{

    public HTTPProxyHandler(String localOrigin, String remoteOrigin) {
        super(localOrigin, remoteOrigin);
    }

    @Override
    public void handleASAPRequest(String path, HttpExchange context) throws Exception{
        HttpURLConnection con = (HttpURLConnection) new URL(path).openConnection();

        con.setRequestMethod(context.getRequestMethod());

        /*
        for (String s:context.getRequestHeaders().keySet()){
            StringBuilder builder=new StringBuilder();
            for (String s2:context.getRequestHeaders().get(s)){
                builder.append(",").append(s2);
            }
            String v=builder.toString().replaceFirst(",","");
            //con.setRequestProperty(s,v);
        }

         */

        InputStream in = con.getInputStream();
        byte[] data = this.fixRequestOrigin(in.readAllBytes());

        context.sendResponseHeaders(con.getResponseCode(), data.length);
        context.getResponseBody().write(data);
        in.close();
        con.disconnect();
    }
}
