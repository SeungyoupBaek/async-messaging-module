package com.youp.async_messaging_module.queue;

import com.youp.async_messaging_module.task.RetryTask;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Component
public class DeadLetterQueueImpl implements DeadLetterQueue {

    private final DelayQueue<RetryTask> deadLetterQueue;

    public DeadLetterQueueImpl() {
        this.deadLetterQueue = new DelayQueue<>();
    }

    @Override
    public RetryTask take() throws InterruptedException {
        return deadLetterQueue.take();
    }

    @Override
    public void submitFailedTask(Runnable task, int retries) {
        RetryTask retryTask = new RetryTask(task, retries, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
        deadLetterQueue.put(retryTask);
    }
}
