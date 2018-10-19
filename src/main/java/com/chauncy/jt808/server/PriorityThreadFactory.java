package com.chauncy.jt808.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chauncy on 2018/6/11.
 */
public class PriorityThreadFactory implements ThreadFactory {


    private int prio;
    private String name;
    private AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup group;


    public PriorityThreadFactory(int prio, String name) {
        this.prio = prio;
        this.name = name;
        this.group = new ThreadGroup(name);
    }


    public ThreadGroup getGroup() {
        return group;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r);
        t.setName(name + "-" + "#-" + threadNumber.getAndIncrement());
        t.setPriority(prio);
        return t;
    }
}
