package com.youp.async_messaging_module.queue;

import com.youp.async_messaging_module.task.RetryTask;

public interface DeadLetterQueue {

    RetryTask take() throws InterruptedException;

    void submitFailedTask(Runnable task, int retries);
}
