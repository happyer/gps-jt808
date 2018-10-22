package com.chauncy.test;

import com.chauncy.jt808.util.BitUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by chauncy on 2018/5/30.
 */
public class KMClient {

    private final String host;
    private final int port;

    public KMClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new KMClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    private static String heartData = "7E 00 02 00 00 01 86 57 11 59 29 00 01 69 7E";


    private static String tcpData = "7E 02 00 00 22 01 44 00 44 00 55 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18 05 30 17 31 36 25 04 00 00 00 00 69 7E";


    private static String batchLocation = "7e0704012f0145314385390024000501003a000001000000080001cd7d01c207278128000000000002180606094227010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080101cd7d01c207278128000000000002180606094232010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080101cd7d01c207278128000000000000180606094258010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080001cd7d01c207278128000000000006180606094812010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080001cd7d01c207278128000000000006180606094828010400000000eb16000c00b28986040419179068187500060089ffffffff377e";


    public static ByteBuf getDate() {
        ByteBuf byteBuf = Unpooled.buffer();
        for (String s : tcpData.split(" ")) {
            byteBuf.writeByte(BitUtils.hexStringToByteArray(s)[0]);
        }
        return byteBuf;
    }


    public static ByteBuf getHeart() {
        ByteBuf byteBuf = Unpooled.buffer();
        for (String s : heartData.split(" ")) {
            byteBuf.writeByte(BitUtils.hexStringToByteArray(s)[0]);
        }
        return byteBuf;
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
//        String host = "115.29.174.15";
        int port = 6688;
        KMClient kmClient = new KMClient(host, port);

//        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
//            HeartHelper.getChannels().forEach(channel -> {
//                channel.writeAndFlush(getDate());
//                System.out.println("start send heart");
//            });
//        }, 0, 1, TimeUnit.SECONDS);
        kmClient.start();


    }


}
