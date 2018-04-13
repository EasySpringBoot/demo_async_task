package com.easy.springboot.demo_async_task

import com.easy.springboot.demo_async_task.task.AsyncTask
import com.easy.springboot.demo_async_task.task.SyncTask
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class DemoAsyncTaskApplicationTests {
    @Autowired lateinit var syncTask: SyncTask
    @Autowired lateinit var asyncTask: AsyncTask

    @Test
    fun testSyncTask() {
        println("开始测试SyncTask")
        val start = System.currentTimeMillis()
        syncTask.doTaskA()
        syncTask.doTaskB()
        syncTask.doTaskC()
        val end = System.currentTimeMillis()
        println("结束测试SyncTask，耗时：" + (end - start) + "ms")
    }

    @Test
    fun testAsyncTask() {
        println("开始测试AsyncTask")
        val start = System.currentTimeMillis()

        val r1 = asyncTask.doTaskA()
        val r2 = asyncTask.doTaskB()
        val r3 = asyncTask.doTaskC()

        while (true) {
            // 三个任务都调用完成，退出循环等待
            if (r1.isDone && r2.isDone && r3.isDone) {
                break
            }
            Thread.sleep(100)
        }

        val end = System.currentTimeMillis()
        println("结束测试AsyncTask，耗时：" + (end - start) + "ms")
    }

}
