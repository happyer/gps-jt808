package com.chauncy.jt808.server;

import java.util.concurrent.TimeUnit;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chauncy.jt808.common.Command;
import com.chauncy.jt808.service.codec.DecoderLoggingOnly;
import com.chauncy.jt808.service.handler.TCPServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;

public class TCPServer extends AbstractNettyServer implements IServer {

    private Logger log = LoggerFactory.getLogger(getClass());

    private int port;


    public TCPServer(String serverName, int port) {
        super(serverName);
        this.port = port;
    }


    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("idleStateHandler", new IdleStateHandler(Command.TCP_CLIENT_IDLE_MINUTES, 0, 0, TimeUnit.MINUTES));
        pipeline.addLast(new DecoderLoggingOnly());
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[]{0x7e}), Unpooled.copiedBuffer(new byte[]{0x7e, 0x7e})));
        pipeline.addLast(new TCPServerHandler());
    }


    @Override
    public void startServer() throws Exception {
        bind(port);
    }

    @Override
    public void stopServer() throws Exception {
        unbind();
    }


    public static void main(String[] args) throws Exception {
        TCPServer server = new TCPServer("tcp-server",6688);
        server.startServer();

    }
}