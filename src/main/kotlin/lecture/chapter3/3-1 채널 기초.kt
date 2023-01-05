/*
* [Chapter 3] 코틀린 채널
*
* [3-1] 채널 기초
* */

package lecture.chapter3

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import lecture.Example
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

/**
 * 예제 80: 채널
 *
 * 채널은 일종의 파이프이다.
 * 송신측에서 채널에 send() 로 데이터를 전달하고,
 * 수신측에서 채널을 통해 receive() 로 데이터를 받는다.
 * (trySend()와 tryReceive() 도 있다. 과거에는 null 을 반환하는 offer() 와 poll() 가 있었다.)
 * (일반적으로 try() send(), 특별한 경우 trySend() tryReceive() 를 쓴다)
 * */
object Example80 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..10) {
                channel.send(x) // suspension point
            }
        }

        repeat(10) {
            println(channel.receive()) // suspension point
        }

        println("완료")
    }
}

/**
 * 예제 81: 같은 코루틴에서 채널을 읽고 쓰면?
 *
 * send() 나 receive() 가 suspension point 이고 서로에게 의존적이기 때문에
 * 같은 코루틴에서 사용하는 것은 위험할 수 있다.
 *
 * 아래 예제는 무한으로 대기하는 경우다.
 * */
object Example81 : Example {

    override fun run() = runBlocking<Unit> {
        val channel = Channel<Int>()

        launch {
            for (x in 1..10) {
                channel.send(x) // 여기서 잠든다
            }

            repeat(10) {
                println(channel.receive()) // 여기서 잠든다
            }

            println("완료")
        }
    }
}

/**
 * 예제 82: 채널 close
 *
 * 채널에서 더 이상 보낼 자료가 없으면 close() 메서드를 이용해 채널을 닫을 수 있다.
 * 채널은 for in 을 이용해서 반복적으로 receive() 할 수 있고,
 * close() 되면 for in 은 자동으로 종료된다.
 * */
object Example82 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..10) {
                channel.send(x)
            }
            channel.close()
        }

        for (x in channel) { // 단, close() 가 없다면 무한 루프가 된다
            println(x)
        }

        println("완료")
    }
}

/**
 * 예제 83: 채널 프로듀서
 *
 * 생산자(producer)와 소비자(consumer)는 굉장히 일반적인 패턴이다.
 * 채널을 이용해서 한 쪽에서 데이터를 만들고 다른 쪽에서 받는 것을 도와주는 확장 함수들이 있다.
 *
 * 1. produce() 코루틴을 만들고 채널을 제공한다.
 * 2. consumeEach() 채널에서 반복해서 데이터를 제공한다.
 *
 * [ProducerScope] 는 [CoroutineScope] 인터페이스와 [SendChannel] 인터페이스를 함께 상속받는다.
 * 따라서, [CoroutineContext] 와 몇가지 [Channel] 인터페이스를 같이 사용할 수 있는 특이한 스코프이다.
 *
 * ● 참고
 *
 * 우리가 흔히 쓰는 runBlocking() 은 [BlockingCoroutine] 을 쓰는데 이는 [AbstractCoroutine] 을 상속받고 있다.
 * 결국 코루틴 빌더는 코루틴을 만드는데, 이들이 코루틴 스코프이기도 한 것이다.
 *
 * [AbstractCoroutine] 은 [JobSupport], [Job] 인터페이스, [Continuation] 인터페이스, [CoroutineScope] 인터페이스를 상속받는다.
 *
 * 여기서 [Continuation] 은 다음에 무엇을 할지, [Job] 은 제어를 위한 정보와 제어, [CoroutineScope] 는 컨텍스트 제공의 역할을 한다.
 * [JobSupport] 는 잡의 실무(?)를 한다고 볼 수 있다.
 * */
object Example83 : Example {

    override fun run() = runBlocking {
        val oneToTen = produce {
            for (x in 1..10) {
                channel.send(x)
            }
        }

        oneToTen.consumeEach {
            println(it)
        }

        println("완료")
    }
}