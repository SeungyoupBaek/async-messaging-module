package com.youp.async_messaging_module.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueImpl implements DeadLetterQueue {

    private final DelayQueue<RetryTask> deadLetterQueue;

    private final TaskQueue taskQueue;

    public DeadLetterQueueImpl(TaskQueue taskQueue) {
        this.deadLetterQueue = new DelayQueue<>();
        this.taskQueue = taskQueue;
        startRetryConsumer();
    }

    @Override
    public void submitFailedTask(Runnable task, int retries) {
        RetryTask retryTask = new RetryTask(task, retries, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
        deadLetterQueue.put(retryTask);
    }

    private void startRetryConsumer() {
        Runnable consumerTask = () -> {
            while (true) {
                try {
                    RetryTask retryTask = deadLetterQueue.take();
                    boolean submitted = taskQueue.submitTask(retryTask.getTask());
                    if (!submitted) {
                        submitFailedTask(retryTask.getTask(), retryTask.getRetries() + 1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread consumerTread = new Thread(consumerTask);
        consumerTread.setDaemon(true);
        consumerTread.start();
    }

    @Getter
    private static class RetryTask implements Delayed {

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
}
