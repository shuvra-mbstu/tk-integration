package com.tk.integration.common.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);
    @Bean(name="processExecutor")
    public Executor processExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);  // Set the core thread pool size
        executor.setMaxPoolSize(10);  // Set the max thread pool size
        executor.setQueueCapacity(500);  // Queue capacity for tasks
        executor.setThreadNamePrefix("ProcessExecutor-");
        executor.initialize();
        return executor;
    }
}