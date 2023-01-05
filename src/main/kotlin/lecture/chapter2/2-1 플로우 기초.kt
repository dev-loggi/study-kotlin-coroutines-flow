/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-1] 플로우 기초
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import lecture.Example
import kotlin.random.Random

/**
 * 예제 45: 처음 만나보는 플로우
 *
 * [Flow] 는 코틀린에서 쓸 수 있는 비동기 스트림입니다.
 *
 * flow() 플로우 빌더 함수를 이용해서 코드 블록을 구성하고,
 * emit() 을 호출해서 스트림에 데이터를 흘려보낸다.
 *
 * 플로우는 콜드 스트림이기 때문에 요청 측에서 collect() 를 호출해야 값을 발생시키기 시작한다.
 *
 * - 콜드 스트림: 요청이 있는 경우에 보통 1:1로 값을 전달하기 시작
 * - 핫 스트림: 0개 이상의 상대를 향해 지속적으로 값을 전달.
 * */
object Example45 : Example {

    override fun run() = runBlocking {
        flowSomething().collect { value ->
            println(value)
        }
    }

    private fun flowSomething(): Flow<Int> = flow {
        repeat(10) {
            emit(Random.nextInt(0, 500)) // 방출
            delay(10L)
        }
    }
}

/**
 * 예제 46: 플로우 취소
 *
 * 코루틴 시간에 배웠던 withTimeoutOrNull() 을 이용해 간단히 취소할 수 있다.
 * */
object Example46 : Example {

    override fun run() = runBlocking {
        val result = withTimeoutOrNull(500L) {
            flowSomething().collect { value ->
                println(value)
            }
            true
        } ?: false

        if (!result) {
            println("취소되었습니다.")
        }
    }

    private fun flowSomething(): Flow<Int> = flow {
        repeat(10) {
            emit(Random.nextInt(0, 500)) // 방출
            delay(100L)
        }
    }
}

/**
 * 예제 47: 플로우 빌더 flowOf()
 *
 * flow() 이외에도 flowOf(), asFlow() 등의 몇가지 플로우 빌더가 있다.
 * 먼저 flowOf() 를 살펴보자.
 *
 * flowOf() 는 여러 값을 인자로 전달해 플로우를 만든다.
 * */
object Example47 : Example {

    override fun run() = runBlocking {
        flowOf(1, 2, 3, 4, 5).collect { value ->
            println(value)
        }
    }
}

/**
 * 예제 49: 플로우 빌더 asFlow()
 *
 * asFlow() 는 컬렉션이나 시퀀스를 전달해 플로우를 만들 수 있다.
 * */
object Example48 : Example {

    override fun run() = runBlocking {
        listOf(1, 2, 3, 4, 5).asFlow().collect { value ->
            println(value)
        }
        (6..10).asFlow().collect {
            println(it)
        }
    }
}