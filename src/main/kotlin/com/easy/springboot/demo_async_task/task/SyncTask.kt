package com.easy.springboot.demo_async_task.task

import org.springframework.stereotype.Component

@Component
class SyncTask {

    fun doTaskA() {
        println("开始任务A")
        val start = System.currentTimeMillis()
        Thread.sleep(1000)
        val end = System.currentTimeMillis()
        println("结束任务A，耗时：" + (end - start) + "ms")
    }

    fun doTaskB() {
        println("开始任务B")
        val start = System.currentTimeMillis()
        Thread.sleep(2000)
        val end = System.currentTimeMillis()
        println("结束任务B，耗时：" + (end - start) + "ms")
    }

    fun doTaskC() {
        println("开始任务C")
        val start = System.currentTimeMillis()
        Thread.sleep(3000)
        val end = System.currentTimeMillis()
        println("结束任务C，耗时：" + (end - start) + "ms")
    }
}