# demo_async_task


“异步”(Asynchronous)与“同步”(Synchronous)相对，异步不用阻塞当前线程来等待处理完成，而是允许后续操作，直至其它线程将处理完成，并回调通知此线程。也就是说，异步永远是非阻塞的(non-blocking)。

同步操作的程序，会按照代码的顺序依次执行，每一行程序都必须等待上一个程序执行完成之后才能执行。哪些情况建议使用同步交互呢？例如，银行的转账系统，对数据库的保存操作等等，都会使用同步交互操作。

异步操作的程序，在代码执行时，不等待异步调用的语句返回结果就执行后面的程序。当任务间没有先后顺序依赖逻辑的时候，可以使用异步。异步编程的主要困难在于，构建程序的执行逻辑时是非线性的，这需要将任务流分解成很多小的步骤，再通过异步回调函数的形式组合起来。

1.同步任务执行

下面通过一个简单示例来直观的理解什么是同步任务执行。

编写SyncTask类

编写一个 SyncTask 类，创建三个处理函数：doTaskA()、doTaskB()、doTaskC() 来分别模拟三个任务执行的操作，操作消耗时间分别设置为：1000ms、2000ms、3000ms。代码如下

```kotlin
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
```


单元测试

下面我们来写一个单元测试，在测试用例中顺序执行 doTaskA()、doTaskB()、doTaskC() 三个函数。

```kotlin
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

}

```

执行上面的单元测试，可以在控制台看到类似如下输出：

开始测试SyncTask
开始任务A
结束任务A，耗时：1004ms
开始任务B
结束任务B，耗时：2005ms
开始任务C
结束任务C，耗时：3002ms
结束测试SyncTask，耗时：6012ms

任务A、任务B、任务C 依次按照其先后顺序执行完毕，总共耗时：6012ms。


2.异步任务执行

上面的同步任务的执行，虽然顺利的执行完了三个任务，但我们可以看到执行时间比较长，是这3个任务时间的累加。若这三个任务本身之间不存在依赖关系，可以并发执行的话，同步顺序执行在执行效率上就比较差了——这个时候，我们可以考虑通过异步调用的方式来实现“异步并发”地执行。

编写AsyncTask类

在Spring Boot中，我们只需要通过使用@Async注解就能简单的将原来的同步函数变为异步函数，编写AsyncTask类，代码改写如下：

```kotlin
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
```

上面的异步执行的任务，都返回一个 Future<String> 类型的结果对象 AsyncResult。这个对象中保存了任务的执行状态。我们可以通过轮询这个结果来等待任务执行完毕，这样我们可以在上面3个任务都执行完毕后，再继续做一些事情。

自定义线程池

其中，asyncTaskExecutor是我们自定义的线程池。代码如下

```kotlin
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

```

上面的代码中，我们通过使用ThreadPoolTaskExecutor创建了一个线程池，同时设置了以下这些参数，说明如下表：

核心线程数10：线程池创建时候初始化的线程数
最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
缓冲队列100：用来缓冲执行任务的队列
允许线程的空闲时间30秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务

启用 @EnableAsync

为了让@Async注解能够生效，还需要在Spring Boot 的入口类上配置 @EnableAsync，代码如下：

```kotlin
@SpringBootApplication
@EnableAsync
open class DemoAsyncTaskApplication

fun main(args: Array<String>) {
    runApplication<DemoAsyncTaskApplication>(*args)
}
```

单元测试

同样地，我们来编写一个单元测试用例来测试一下异步执行这3个任务所花费的时间。代码如下


```kotlin
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

```

我们使用了一个死循环来等待三个任务都调用完成，当满足条件 r1.isDone && r2.isDone && r3.isDone 就退出循环等待。
执行上面的测试代码，可以在控制台看到类似如下输出

开始测试AsyncTask
开始任务A
开始任务B
开始任务C
结束任务A，耗时：1002ms
结束任务B，耗时：2004ms
结束任务C，耗时：3004ms
结束测试AsyncTask，耗时：3125ms

我们可以看到，通过异步调用，任务A、任务B、任务C 异步执行完毕总共耗时： 3125ms。 相比于同步执行，无疑大大的减少了程序的总运行时间。


提示：本节实例工程源代码 https://github.com/EasySpringBoot/demo_async_task


