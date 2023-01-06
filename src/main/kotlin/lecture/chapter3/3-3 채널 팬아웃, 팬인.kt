/*
* [Chapter 3] 코틀린 채널
*
* [3-3] 채널 팬아웃, 팬인
* */

package lecture.chapter3

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select
import lecture.Example
import kotlin.system.measureTimeMillis

/**
 * 예제 87: 팬 아웃
 *
 * 여러 코루틴이 동시에 채널을 구독할 수 있다.
 * */
object Example87 : Example {

    override fun run() = runBlocking {
        val producer = produceNumbers()

        repeat(5) { id ->
            processNumber(id, producer)
        }

        delay(1500L)
        producer.cancel()
    }

    private fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        var x = 1
        while (true) {
            send(x++)
            delay(100)
        }
    }

    private fun CoroutineScope.processNumber(id: Int, channel: ReceiveChannel<Int>): Job = launch {
        channel.consumeEach {
            println("$id 가 $it 을 받았습니다.")
        }
    }
}

/**
 * 예제 88: 팬 인
 *
 * 팬 인은 반대로 생산자가 많은 것이다.
 *
 * (생각해 볼 것)
 * - coroutineContext 의 자식이 아닌 본인을 취소하면 어떻게 될까요?
 * - processNumber 를 suspend 함수의 형태로 변형하면 어떻게 될까요?
 * - 다른 방법으로 취소할 수 있을까요?
 * */
object Example88 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>()

        val producerJob1 = launch {
            produceNumbers(channel, 1, 100) // 1, 3, 5, 7, ... (per 100ms)
        }
        val producerJob2 = launch {
            produceNumbers(channel, 2, 150) // 2, 4, 7, 8, ... (per 150ms)
        }

        // 생산자 2, 소비자 1
        val consumerJob = processNumber(channel)
        delay(1000)
        coroutineContext.cancelChildren()
//        producerJob1.cancel()
//        producerJob2.cancel()
//        consumerJob.cancel()
    }

    private suspend fun produceNumbers(channel: SendChannel<Int>, from: Int, interval: Long) {
        var x = from
        while (true) {
            channel.send(x)
            x += 2
            delay(interval)
        }
    }

    private fun CoroutineScope.processNumber(channel: ReceiveChannel<Int>): Job = launch {
        channel.consumeEach {
            println("$it 을 받았습니다")
        }
    }
}

/**
 * 예제 89: 공정한 채널
 *
 * 두 개의 코루틴에서 채널을 서로 사용할 때, 공정하게 기회를 준다는 것을 알 수 있다.
 * */
object Example89 : Example {

    override fun run() = runBlocking {
        val channel = Channel<String>()

        launch {
            someone(channel, "민준", 500)
        }
        launch {
            someone(channel, "서연", 1000)
        }

        channel.send("패스트캠퍼스")
        delay(10000)
        coroutineContext.cancelChildren()
    }

    private suspend fun someone(channel: Channel<String>, name: String, interval: Long) {
        for (comment in channel) {
            println("$name: $comment")
            channel.send(comment.drop(1) + comment.first())
            delay(interval)
        }
    }
}

/**
 * 예제 90: select()
 *
 * 먼저 끝나는 요청을 처리하는 것이 중요할 수 있다.
 * 이 경우에 select() 를 사용하면 된다.
 * */
object Example90 : Example {

    override fun run() = runBlocking {
        val fasts = sayFast()
        val campuses = sayCampus()

        repeat(10) {
            select {
                fasts.onReceive {
                    println("fast: $it")
                }
                campuses.onReceive {
                    println("campus: $it")
                }
            }
        }

        coroutineContext.cancelChildren()
    }

    private fun CoroutineScope.sayFast(): ReceiveChannel<String> = produce {
        while (true) {
            delay(100L)
            send("패스트")
        }
    }

    private fun CoroutineScope.sayCampus(): ReceiveChannel<String> = produce {
        while (true) {
            delay(200L)
            send("캠퍼스")
        }
    }
}