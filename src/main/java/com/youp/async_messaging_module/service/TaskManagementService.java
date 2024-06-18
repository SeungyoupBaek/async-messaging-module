package com.youp.async_messaging_module.service;

import com.youp.async_messaging_module.monitor.MemoryMonitor;
import com.youp.async_messaging_module.queue.DeadLetterQueue;
import com.youp.async_messaging_module.queue.TaskQueue;
import com.youp.async_messaging_module.task.RetryTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TaskManagementService {

    private final TaskQueue taskQueue;

    private final DeadLetterQueue deadLetterQueue;

    private final TaskExecutor taskExecutor;

    public TaskManagementService(ThreadPoolTaskExecutor taskExecutor, TaskQueue taskQueue, DeadLetterQueue deadLetterQueue) {
        this.taskQueue = taskQueue;
        this.deadLetterQueue = deadLetterQueue;
        this.taskExecutor = taskExecutor;
        startTaskConsumer();
        startRetryConsumer();
    }

    public boolean submitTask(Runnable task) {
        if (MemoryMonitor.isMemoryUsageHigh()) {
            log.info("Memory usage is high. Task rejected");
            return false;
        }

        boolean added = taskQueue.offer(task);

        if (!added) {
            log.error("Task queue is full");
        }

        return added;
    }

    private void startTaskConsumer() {
        Runnable consumerTask = () -> {
            while (true) {
                try {
                    Runnable task = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        try {
                            taskExecutor.execute(task);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            deadLetterQueue.submitFailedTask(task, 0);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread consumerThread = new Thread(consumerTask);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    private void startRetryConsumer() {
        Runnable consumerTask = () -> {
            while (true) {
                try {
                    RetryTask retryTask = deadLetterQueue.take();
                    boolean submitted = submitTask(retryTask.getTask());
                    if (!submitted) {
                        deadLetterQueue.submitFailedTask(retryTask.getTask(), retryTask.getRetries() + 1);
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
}
