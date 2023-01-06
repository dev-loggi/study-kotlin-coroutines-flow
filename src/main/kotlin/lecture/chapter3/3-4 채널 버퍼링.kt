/*
* [Chapter 3] 코틀린 채널
*
* [3-4] 채널 버퍼링
* */

package lecture.chapter3

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 91: 버퍼
 *
 * 이전에 만들었던 예제를 확장하여 버퍼를 지정해보자.
 * [Channel] 생성자는 인자로 버퍼의 사이즈를 지정 받는다.
 * (지정하지 않으면 버퍼를 생성하지 않는다.)
 * */
object Example91 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>(10) // 채널의 버퍼 사이즈: 10

        launch {
            for (x in 1..20) {
                println("$x 전송중")
                channel.send(x)
            }
            channel.close()
        }

        for (x in channel) {
            println("$x 수신")
            delay(100)
        }

        println("완료")
    }
}

/**
 * 예제 92: 랑데뷰
 *
 * 버퍼 사이즈를 랑데뷰[Channel.RENDEZVOUS]로 지정해봅시다.
 * (랑데뷰는 프랑스어로 만남, 예약 이라는 뜻이다.)
 *
 * 랑데뷰는 버퍼 사이즈를 0으로 지정하는 것이다.
 * 생성자에 사이즈를 전달하지 않으면 랑데뷰가 디폴트이다.
 *
 * 이외에도 사이즈 대신 사용할 수 있는 다른 설정 값이 있다.
 * - [Channel.UNLIMITED]: 무제한으로 설정
 * - [Channel.CONFLATED]: 오래된 값이 지워짐
 * - [Channel.BUFFERED]: 64개의 버퍼. 오버플로우엔 suspend
 * */
object Example92 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>(Channel.RENDEZVOUS) // 버퍼 사이즈: 0

        launch {
            for (x in 1..20) {
                println("$x 전송중")
                channel.send(x)
            }
        }

        for (x in channel) {
            println("$x 수신")
            delay(100)
        }

        println("완료")
    }
}

/**
 * 예제 93: 버퍼 오버플로우
 *
 * 버퍼의 오버플로우 정책에 따라 다른 결과가 나올 수 있다.
 *
 * - [BufferOverflow.SUSPEND]: 잠들었다가 깨어난다
 * - [BufferOverflow.DROP_OLDEST]: 예전 데이터를 지운다
 * - [BufferOverflow.DROP_LATEST]: 새 데이터를 지운다
 * */
object Example93 : Example {

    override fun run() = runBlocking {
        val channel = Channel<Int>(2, BufferOverflow.DROP_OLDEST)

        launch {
            for (x in 1..50) {
                channel.send(x)
            }
            channel.close()
        }

        delay(500)

        for (x in channel) {
            println("$x 수신")
            delay(100)
        }

        println("완료")
    }
}