package com.chauncy.test;

import com.chauncy.jt808.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by chauncy on 2018/5/30.
 */
public class KMClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private String tcpData = "7E 02 00 00 22 01 44 00 44 00 55 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18 05 30 17 31 36 25 04 00 00 00 00 69 7E";

    private static String batchLocation = "7e0704012f0145314385390024000501003a000001000000080001cd7d01c207278128000000000002180606094227010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080101cd7d01c207278128000000000002180606094232010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080101cd7d01c207278128000000000000180606094258010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080001cd7d01c207278128000000000006180606094812010400000000eb16000c00b28986040419179068187500060089ffffffff003a000001000000080001cd7d01c207278128000000000006180606094828010400000000eb16000c00b28986040419179068187500060089ffffffff377e";


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//        for (int i = 0 ;i <10;i++){
//        ctx.writeAndFlush(get());
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //粘包测试
//        for (int i = 0; i < 20; i++) {
//            ctx.writeAndFlush(get());
//        }
        //end 粘包测试


        //拆包测试
//        for (int i = 0; i < 20; i++) {
//            ctx.writeAndFlush(generator());
//        }
        //end拆包测试

        //batch location
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(StringUtils.getByteBuf(batchLocation));
        }


//        ctx.writeAndFlush(KMClient.getHeart());

        HeartHelper.add(ctx.channel());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private ByteBuf get() {
        ByteBuf byteBuf = Unpooled.buffer();
        for (String s : tcpData.split(" ")) {
            byteBuf.writeByte(hexStringToByteArray(s)[0]);
        }
        return byteBuf;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    /**
     * 1024+12+1+2
     *
     * @return
     */
    public static ByteBuf generator() {
        byte b = 0x7e;
        short id = 0x0200;
        short attr = 0x01ff;
        byte[] phone = new byte[]{0x01, 0x44, 0x00, 0x44, 0x00, 0x55};
        short number = 1;
        byte[] body = new byte[511];
        for (int i = 0; i < 511; i++) {
            body[i] = 0;
        }
        byte crc = 0x69;
        byte tail = 0x7e;
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(b);
        byteBuf.writeShort(id);
        byteBuf.writeShort(attr);
        byteBuf.writeBytes(phone);
        byteBuf.writeShort(number);
        byteBuf.writeBytes(body);
        byteBuf.writeByte(crc);
        byteBuf.writeByte(tail);
        return byteBuf;
    }

}
