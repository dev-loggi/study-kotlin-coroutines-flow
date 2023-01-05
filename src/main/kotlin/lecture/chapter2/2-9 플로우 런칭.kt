/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-9] 플로우 런칭
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 78: 이벤트를 Flow 로 처리하기
 *
 * addEventListener 대신 플로우의 onEach() 를 사용할 수 있다.
 * 이벤트마다 onEach() 가 대응하는 것이다.
 *
 * 하지만, collect() 는 Flow 가 끝날 때까지 기다리는 것이 문제이다.
 * */
object Example78 : Example {

    override fun run() = runBlocking {
        events()
            .onEach { event -> println("Event: $event") }
            .collect() // 여기서 플로우가 끝날 때까지 기다린다.

        println("Done") // 플로우가 끝나면 Done 출력
    }

    fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }
}

/**
 * 예제 79: launchIn() 을 사용하여 런칭하기
 *
 * launchIn() 을 이용하면 별도의 코루틴에서 플로우를 런칭할 수 있다.
 * */
object Example79 : Example {

    override fun run() = runBlocking {
        events()
            .onEach(::log)
            .launchIn(this) // 새로운 코루틴을 런칭함

        log("Done") // 바로 Done 출력
    }

    fun events(): Flow<Int> = (1..20).asFlow().onEach { delay(100) }

    fun log(msg: Any) = println("${Thread.currentThread()} $msg")
}