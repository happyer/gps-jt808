package com.chauncy.jt808.service.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chauncy.jt808.util.HexStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 该解码器只是为了自己日志所用,没其他作用.<br>
 * 最终删除
 * 
 * @author hylexus
 *
 */
public class DecoderLoggingOnly extends ByteToMessageDecoder {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		String hex = bufStr(in);
		log.info("ip={},hex = {}", ctx.channel().remoteAddress(), hex);

		ByteBuf buf = Unpooled.buffer();
		while (in.isReadable()) {
			buf.writeByte(in.readByte());
		}
		out.add(buf);
	}

	private String bufStr(ByteBuf in) {
		byte[] dst = new byte[in.readableBytes()];
		in.getBytes(0, dst);
		return HexStringUtils.toHexString(dst);
	}
}
