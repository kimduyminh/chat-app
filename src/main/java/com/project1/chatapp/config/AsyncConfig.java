package com.project1.chatapp.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "AsyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20); // Min threads
        executor.setMaxPoolSize(200); // Max threads
        executor.setQueueCapacity(50); // Max queue
        executor.setThreadNamePrefix("chatapp-executor");
        executor.initialize();
        return executor;
    }
}
