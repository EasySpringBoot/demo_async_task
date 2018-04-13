package com.easy.springboot.demo_async_task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor


@SpringBootApplication
@EnableAsync
open class DemoAsyncTaskApplication

fun main(args: Array<String>) {
    runApplication<DemoAsyncTaskApplication>(*args)
}


@Configuration
open class TaskExecutorPoolConfig {
    @Bean("asyncTaskExecutor")
    open fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10 //线程池维护线程的最少数量
        executor.maxPoolSize = 20 //线程池维护线程的最大数量
        executor.setQueueCapacity(100)
        executor.keepAliveSeconds = 30 //线程池维护线程所允许的空闲时间,TimeUnit.SECONDS
        executor.threadNamePrefix = "asyncTaskExecutor-"
        // 线程池对拒绝任务的处理策略: CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        return executor
    }
}

