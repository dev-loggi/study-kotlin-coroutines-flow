/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-4] 플로우 버퍼링
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * 예제 62: 버퍼가 없는 플로우
 *
 * 보내는 쪽과 받는 쪽이 모두 바쁘다고 가정해봅시다.
 * */
object Example62 : Example {

    override fun run() = runBlocking {
        val time = measureTimeMillis {
            simple().collect { value ->
                delay(300)
                println(value)
            }
        }
        println("Collected in $time ms")
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }
}

/**
 * 예제 63: buffer()
 *
 * buffer() 로 버퍼를 추가해 보내는 측이 더이상 기다리지 않게 합니다.
 * */
object Example63 : Example {

    override fun run() = runBlocking {
        val time = measureTimeMillis {
            simple().buffer()
                .collect { value ->
                    delay(300)
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }
}

/**
 * 예제 64: conflate()
 *
 * conflate() 를 이용하면 중간의 값을 융합(conflate)할 수 있습니다.
 * 처리보다 빨리 생성한 데이터의 중간 값들을 누락시킵니다.
 * */
object Example64 : Example {

    override fun run() = runBlocking {
        val time = measureTimeMillis {
            simple().conflate()
                .collect { value ->
                    delay(300)
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }
}

/**
 * 예제 65: collectLatest(), 마지막 값만 처리하기
 *
 * collectLatest() 는 수신 측에서 데이터를 처리하고 있는 동안에 새로운 데이터가 전송이되는 경우,
 * 해당 데이터 처리는 취소(누락)하고 다시 데이터 처리를 시작합니다.
 * */
object Example65 : Example {

    override fun run() = runBlocking {
        val time = measureTimeMillis {
            simple().collectLatest { value ->
                println("값 $value 를 처리하기 시작합니다")
                delay(300)
                println("처리를 완료하였습니다(value=$value)")
            }
        }
        println("Collected in $time ms")
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(Random.nextLong(200, 400))
            emit(i)
        }
    }
}