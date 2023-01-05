/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-3] 플로우 컨텍스트
* */

package lecture.chapter2

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import lecture.Example

/**
 * 예제 59: 플로우는 코루틴 컨텍스트에서
 *
 * 플로우는 현재 코루틴 컨텍스트에서 호출된다.
 * */
object Example59 : Example {

    override fun run() = runBlocking<Unit> {
        launch(Dispatchers.IO) {
            simple()
                .collect { value -> log("$value 를 받음") }
        }
    }

    fun simple(): Flow<Int> = flow {
        log("flow 를 시작합니다.")
        for (i in 1..10) {
            emit(i)
        }
    }

    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
}

/**
 * 예제 60: 다른 컨텍스트로 옮겨갈 수 없는 플로우
 * */
object Example60 : Example {

    override fun run() = runBlocking<Unit> {
        launch(Dispatchers.IO) {
            simple()
                .collect { value -> log("$value 를 받음") }
        }
    }

    fun simple(): Flow<Int> = flow {
        // Error: 플로우 내에서는 컨텍스트를 바꿀 수 없음
        withContext(Dispatchers.Default) {
            log("flow 를 시작합니다.")
            for (i in 1..10) {
                emit(i)
            }
        }
    }

    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
}

/**
 * 예제 61: flowOn() 연산자
 *
 * flowOn() 연산자를 통해 먼텍스트를 올바르게 바꿀 수 있다.
 * */
object Example61 : Example {

    override fun run() = runBlocking {
        simple().collect { value ->
            log("$value 를 받음")
        } // 다운 스트림, main
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100L)
            log("값 $i 를 emit 합니다")
            emit(i)
        } // 업 스트림, Dispatchers.IO
    }
        .flowOn(Dispatchers.IO)
        .map {
            log("map: $it -> ${it * 2}")
            it * 2
        } // 업 스트림, Dispatchers.Default
        .flowOn(Dispatchers.Default)

    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
}