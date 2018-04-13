package com.easy.springboot.demo_async_task.task

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.Future

@Component
open class AsyncTask {

    @Async("asyncTaskExecutor")
    open fun doTaskA(): Future<String> {
        println("开始任务A")
        val start = System.currentTimeMillis()
        Thread.sleep(1000)
        val end = System.currentTimeMillis()
        println("结束任务A，耗时：" + (end - start) + "ms")
        return AsyncResult("TaskA DONE")
    }

    @Async("asyncTaskExecutor")
    open fun doTaskB(): Future<String> {
        println("开始任务B")
        val start = System.currentTimeMillis()
        Thread.sleep(2000)
        val end = System.currentTimeMillis()
        println("结束任务B，耗时：" + (end - start) + "ms")
        return AsyncResult("TaskB DONE")
    }

    @Async("asyncTaskExecutor")
    open fun doTaskC(): Future<String> {
        println("开始任务C")
        val start = System.currentTimeMillis()
        Thread.sleep(3000)
        val end = System.currentTimeMillis()
        println("结束任务C，耗时：" + (end - start) + "ms")
        return AsyncResult("TaskC DONE")
    }
}