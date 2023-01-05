/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-6] 플로우 플래트닝하기
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 플로우에서는 3가지 유형의 flatMap() 을 지원하고 있다.
 * flatMapConcat(), flatMapMerge(), flatMapLatest()
 *
 * 예제 68: flatMapConcat()
 *
 * flatMapConcat() 은 첫 번째 요소에 대하여 플래트닝을 하고 나서 두 번째 요소를 한다.
 * */
object Example68 : Example {

    override fun run() = runBlocking {
        val startTime = System.currentTimeMillis() // remember the start time

        (1..3).asFlow().onEach { delay(100) }
            .flatMapConcat(::requestFlow)
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms")
            }
    }

    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: First")
        delay(500)
        emit("$i: Second")
    }
}

/**
 * 예제 69: flatMapMerge()
 *
 * flatMapMerge() 는 첫 요소의 플래트닝을 시작하며 이어서 다음 요소의 플래트닝을 시작한다.
 * */
object Example69 : Example {

    override fun run() = runBlocking {
        val startTime = System.currentTimeMillis() // remember the start time

        (1..3).asFlow().onEach { delay(100) }
            .flatMapMerge { requestFlow(it) }
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms")
            }
    }

    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: first")
        delay(500)
        emit("$i: second")
    }
}

/**
 * 예제 70: flatMapLatest()
 *
 * flatMapLatest() 는 다음 요소의 플래트닝을 시작하며 이전에 진행 중이던 플래트닝을 취소한다.
 * */
object Example70 : Example {

    override fun run() = runBlocking {
        val startTime = System.currentTimeMillis()

        (1..5).asFlow().onEach { delay(100) }
            .flatMapLatest(::requestFlow)
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms")
            }
    }

    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: first")
        delay(500)
        emit("$i: second")
    }
}