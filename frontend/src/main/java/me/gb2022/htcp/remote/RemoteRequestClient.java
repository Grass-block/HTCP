package me.gb2022.htcp.remote;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class RemoteRequestClient {
    private static final ILogger LOGGER = LogManager.getLogger("Frontend");
    private final Map<String, HttpResponseData> requests = new HashMap<>();

    private final String host;
    private final int port;
    private final EventLoopGroup group = new NioEventLoopGroup();
    private SocketChannel channel;

    public RemoteRequestClient(String host) throws IOException {
        this.host = host.split(":")[0];
        this.port = Integer.parseInt(host.split(":")[1]);
    }

    public Future<HttpResponseData> sendRequest(String method, String url) {
        String requestRecord = url + method;
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();

        ClientHandler.writeString(buffer, url);
        ClientHandler.writeString(buffer, method);
        this.channel.writeAndFlush(buffer);
        return new RequestFuture(requestRecord, this.requests);
    }

    public void start() {
        new Thread(()->{
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_RCVBUF, 16777216)
                        .handler(this.initializer());
                ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
                LOGGER.info("connected to remote host %s:%s".formatted(this.host, this.port));
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                LOGGER.exception(e);
            } finally {
                LOGGER.info("remote bridge %s:%s closed.".formatted(this.host, this.port));
                this.group.shutdownGracefully();
            }
        }).start();
    }

    private ChannelInitializer<SocketChannel> initializer() {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE,
                        0, 4, 0, 4, true));
                pipeline.addLast(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
                pipeline.addLast(new ClientHandler(RemoteRequestClient.this));
                RemoteRequestClient.this.channel = ch;
            }
        };
    }

    public void addRequest(String url, String method, HttpResponseData data) {
        this.requests.put(url + method, data);
    }

    public void stop() {
        this.group.shutdownGracefully();
    }
}
