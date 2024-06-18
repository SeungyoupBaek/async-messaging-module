package com.youp.async_messaging_module.task;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
public class RetryTask implements Delayed {

    private final Runnable task;

    private final int retries;

    private final long startTime;

    public RetryTask(Runnable task, int retries, long startTime) {
        this.task = task;
        this.retries = retries;
        this.startTime = startTime;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        long delay = startTime - System.currentTimeMillis();
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }
}
