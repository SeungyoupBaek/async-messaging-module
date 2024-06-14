package com.youp.async_messaging_module.queue;

import com.youp.async_messaging_module.monitor.MemoryMonitor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskQueueImpl implements TaskQueue {

    private final BlockingQueue<Runnable> taskQueue;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final DeadLetterQueue deadLetterQueue;

    public TaskQueueImpl(@Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor,
                         DeadLetterQueue deadLetterQueue) {
        this.taskQueue = new LinkedBlockingQueue<>(100); // Limit the queue size
        this.taskExecutor = taskExecutor;
        this.deadLetterQueue = deadLetterQueue;
        startTaskConsumer();
    }

    @Override
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
}
