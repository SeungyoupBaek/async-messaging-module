package com.youp.async_messaging_module.queue;

import java.util.concurrent.TimeUnit;

public interface TaskQueue {

    boolean offer(Runnable task);

    Runnable poll(long timeout, TimeUnit unit) throws InterruptedException;
}
