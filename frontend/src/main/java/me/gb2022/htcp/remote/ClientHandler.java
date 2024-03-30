package me.gb2022.htcp.remote;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final ILogger LOGGER = LogManager.getLogger("RequestHandler");
    private final RemoteRequestClient client;

    public ClientHandler(RemoteRequestClient client) {
        this.client = client;
    }

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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("find exception processing remote response");
        LOGGER.exception((Exception) cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        String url = readString(msg);
        String method = readString(msg);
        int code = msg.readInt();
        byte[] data = readComponent(msg);
        this.client.addRequest(url, method, new HttpResponseData(code, data));
    }
}
