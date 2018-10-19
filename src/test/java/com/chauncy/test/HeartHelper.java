package com.chauncy.test;

import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by chauncy on 2018/6/1.
 */
public class HeartHelper {


    private static Set<Channel> channels = new ConcurrentSkipListSet<>();

    public static void add(Channel channel) {
        channels.add(channel);
    }


    public static Set<Channel> getChannels() {
        return channels;
    }
}
