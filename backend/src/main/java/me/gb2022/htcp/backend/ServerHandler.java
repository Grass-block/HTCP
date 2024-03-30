package me.gb2022.htcp.backend;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final ILogger LOGGER = LogManager.getLogger("RequestHandler");

    static String readString(ByteBuf buffer) {
        return new String(readComponent(buffer), StandardCharsets.UTF_8);
    }

    static void writeString(ByteBuf buffer, String data) {
        byte[] raw = data.getBytes(StandardCharsets.UTF_8);
        writeComponent(buffer, raw);
    }

    static byte[] readComponent(ByteBuf buffer) {
        int len = buffer.readInt();
        byte[] data = new byte[len];
        buffer.readBytes(data);
        return data;
    }

    static void writeComponent(ByteBuf buffer, byte[] data) {
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        String url = readString(msg);
        String method = readString(msg);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod(method);

            int code = con.getResponseCode();
            InputStream in = con.getInputStream();
            byte[] data = in.readAllBytes();
            in.close();
            con.disconnect();

            ByteBuf output = ctx.alloc().buffer();
            output.resetWriterIndex();

            writeString(output, url);
            writeString(output, method);
            output.writeInt(code);
            writeComponent(output, data);
            ctx.writeAndFlush(output);
        } catch (Exception e) {
            LOGGER.error("find error processing remote request: %s  %s".formatted(method, url));
            LOGGER.exception(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.exception((Exception) cause);
    }
}
