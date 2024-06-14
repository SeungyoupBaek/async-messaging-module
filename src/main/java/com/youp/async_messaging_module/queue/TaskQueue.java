package com.youp.async_messaging_module.queue;

public interface TaskQueue {

    boolean submitTask(Runnable task);
}
