/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-7] 플로우 예외처리하기
* */

package lecture.chapter2

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 71: 수집기 측에서 예외처리하기
 *
 * 예외는 collect() 하는 수집기 측에서도 try-catch 구문을 이용해 할 수 있다.
 * */
object Example71 : Example {

    override fun run() = runBlocking {
        try {
            simple().collect { value ->
                println(value)
                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i)
        }
    }
}

/**
 * 예제 72: 모든 예외는 처리 가능
 *
 * 어느 곳에서 발생한 예외라도 처리가 가능하다.
 * */
object Example72 : Example {

    override fun run() = runBlocking {
        try {
            simple().collect(::println)
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }

    fun simple(): Flow<String> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i)
        }
    }
        .map { value ->
            check(value <= 1) { "Crashed on $value" }
            "string $value"
        }
}

/**
 * 예제 73: 예외 투명성
 *
 * 빌더 코드 블록 내에서 예외를 처리하는 것은 예외 투명성을 어기는 것이다.
 * 플로우에서는 catch() 연산자 이용을 권장한다.
 *
 * catch() 블록에서 예외를 새로운 데이터로 만들어 emit() 하거나, 다시 예외를 던지거나, 로그를 남길 수 있다.
 * */
object Example73 : Example {

    override fun run() = runBlocking {
        simple()
            .catch { e -> emit("Caught $e") }
            .collect(::println)
    }

    fun simple(): Flow<String> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i)
        }
    }
        .map { value ->
            check(value <= 1) { "Crashed on $value" }
            "string $value"
        }
}

/**
 * 예제 74: catch 투명성
 *
 * catch() 연산자는 업스트림(catch 연산자를 쓰기 전의 코드)에만 영향을 미치고,
 * 다운스트림에는 영향을 미치지 않는다. 이를 catch 투명성이라 한다.
 * */
object Example74 : Example {

    override fun run() = runBlocking {
        simple()
            .catch { e -> println("Caught $e") } // does not catch downstream exceptions
            .collect { value ->
                check(value <= 1) { "Crashed on $value" }
                println(value)
            }
    }

    fun simple(): Flow<Int> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i)
        }
    }
}