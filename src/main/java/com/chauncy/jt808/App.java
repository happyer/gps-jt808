package com.chauncy.jt808;

import com.chauncy.jt808.common.Command;
import com.chauncy.jt808.server.AbstractNettyServer;
import com.chauncy.jt808.server.IServer;
import com.chauncy.jt808.service.codec.DecoderLoggingOnly;
import com.chauncy.jt808.service.handler.TCPServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by chauncy on 2018/10/19.
 */
public class App extends AbstractNettyServer implements IServer {

    private Logger log = LoggerFactory.getLogger(getClass());

    private int port;


    public App(String serverName, int port) {
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
        App server = new App("tcp-server",6688);
        server.startServer();

    }
}
