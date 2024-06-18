package com.youp.async_messaging_module.configuration;

import com.youp.async_messaging_module.queue.DeadLetterQueue;
import com.youp.async_messaging_module.queue.DeadLetterQueueImpl;
import com.youp.async_messaging_module.queue.TaskQueue;
import com.youp.async_messaging_module.queue.TaskQueueImpl;
import com.youp.async_messaging_module.service.TaskManagementService;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configurable
public class TestConfiguration {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public DeadLetterQueue deadLetterQueue() {
        return new DeadLetterQueueImpl();
    }

    @Bean
    public TaskQueue taskQueue() {
        return new TaskQueueImpl();
    }

    @Bean
    public TaskManagementService taskManagementService(ThreadPoolTaskExecutor taskExecutor, TaskQueue taskQueue, DeadLetterQueue deadLetterQueue) {
        return new TaskManagementService(taskExecutor, taskQueue, deadLetterQueue);
    }
}
