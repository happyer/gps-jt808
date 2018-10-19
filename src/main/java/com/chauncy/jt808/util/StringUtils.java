package com.chauncy.jt808.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.math.BigInteger;

/**
 * Created by chauncy on 2018/6/6.
 */
public class StringUtils {

    public static ByteBuf getByteBuf(String s) {
        ByteBuf src = Unpooled.buffer();
        byte[] b = new BigInteger(s, 16).toByteArray();
        src.writeBytes(b);
        return src;
    }
}
