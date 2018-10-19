package com.chauncy.jt808.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by chauncy on 2018/6/11.
 */
public abstract class AbstractNettyServer {

    private static Logger log = LoggerFactory.getLogger(AbstractNettyServer.class);
    protected static EventLoopGroup bossGroup;
    protected static EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private Channel channel;
    private String serverName;

    private static int processors = Runtime.getRuntime().availableProcessors();

    static {
        if (bossGroup == null || workerGroup == null) {
            if (Epoll.isAvailable()) {
                bossGroup = new EpollEventLoopGroup(processors, new PriorityThreadFactory(Thread.NORM_PRIORITY, "@+main-reactor"));
                workerGroup = new EpollEventLoopGroup(processors * 2, new PriorityThreadFactory(Thread.NORM_PRIORITY, "@+sub-reactor"));
            } else {
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup();
            }
        }
    }

    protected AbstractNettyServer(String serverName) {
        this.serverName = Objects.requireNonNull(serverName, "server name");
        bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator());
        if (Epoll.isAvailable()) {
            bootstrap.channel(EpollServerSocketChannel.class);
            log.info(serverName + " epoll init");
        } else {
            bootstrap.channel(NioServerSocketChannel.class);
            log.info(serverName + " nio init");
        }
        bootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        initPipeline(ch.pipeline());
                    }
                });
    }

    protected abstract void initPipeline(ChannelPipeline pipeline);

    /**
     * bind without block
     *
     * @param port
     */
    protected void bind(int port) {
        try {
            ChannelFuture f = bootstrap.bind(port).addListener((ChannelFuture arg0) -> {
                if (arg0.isSuccess()) {
                    log.info(serverName + " bind at port:" + port);
                    channel = arg0.channel();
                }
            }).sync();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    protected void unbind() {
        if (channel != null) {
            channel.close();
        }
    }

    public Channel channel() {
        return channel;
    }
}
