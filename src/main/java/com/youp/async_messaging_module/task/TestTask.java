package com.youp.async_messaging_module.task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask implements Runnable {

    private final int taskId;

    public TestTask(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        try {
            if (Math.random() < 0.2) {
                throw new RuntimeException("Simulated task failure");
            }
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task " + taskId + " executed");
    }
}
