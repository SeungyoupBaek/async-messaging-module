package com.youp.async_messaging_module.queue;

public interface DeadLetterQueue {

    void submitFailedTask(Runnable task, int retries);
}
