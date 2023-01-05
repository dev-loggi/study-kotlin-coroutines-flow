/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-8] 플로우 완료처리하기
* */

package lecture.chapter2

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 75: 명령형 finally 블록
 *
 * 완료를 처리하는 방법 중의 하나는 명령형 방식으로 finally 블록을 이용하는 것이다.
 * */
object Example75 : Example {

    override fun run() = runBlocking {
        try {
            simple().collect(::println)
        } finally {
            println("Done")
        }
    }

    fun simple(): Flow<Int> = (1..3).asFlow()
}

/**
 * 예제 76: onCompletion(), 선언적으로 완료 처리하기
 *
 * onCompletion() 연산자를 선언해서 완료를 처리할 수 있다.
 * */
object Example76 : Example {

    override fun run() = runBlocking {
        simple()
            .onCompletion { println("Done") }
            .collect(::println)
    }

    fun simple(): Flow<Int> = (1..3).asFlow()
}

/**
 * 예제 77: onCompletion() 의 장점
 *
 * onCompletion() 은 종료 처리를 할 때 예외가 발생되었는지 여부를 알 수 있다.
 * */
object Example77 : Example {

    override fun run() = runBlocking {
        simple()
            .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally") }
            .catch { cause -> println("Caught $cause") }
            .collect(::println)

    }

    fun simple(): Flow<Int> = flow {
        emit(1)
        throw RuntimeException()
    }
}