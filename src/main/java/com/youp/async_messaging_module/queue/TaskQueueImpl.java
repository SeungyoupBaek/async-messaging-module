package com.youp.async_messaging_module.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TaskQueueImpl implements TaskQueue {

    private final BlockingQueue<Runnable> taskQueue;

    public TaskQueueImpl() {
        this.taskQueue = new LinkedBlockingQueue<>(100); // Limit the queue size
    }

    @Override
    public boolean offer(Runnable task) {
        return taskQueue.offer(task);
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        return taskQueue.poll(timeout, unit);
    }
}
