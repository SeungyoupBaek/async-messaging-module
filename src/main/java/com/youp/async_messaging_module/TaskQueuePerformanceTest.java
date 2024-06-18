package com.youp.async_messaging_module;

import com.youp.async_messaging_module.configuration.TestConfiguration;
import com.youp.async_messaging_module.service.TaskManagementService;
import com.youp.async_messaging_module.task.TestTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class TaskQueuePerformanceTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
        TaskManagementService taskManagementService = context.getBean(TaskManagementService.class);

        int numberOfTasks = 1000;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfTasks; i++) {
            boolean submitted = taskManagementService.submitTask(new TestTask(i));
            if (!submitted) {
                System.out.println("Failed to submit task " + i);
            }
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total task execution time: " + (endTime - startTime) + " ms");

        context.close();
    }
}
