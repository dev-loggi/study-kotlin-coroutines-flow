/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-2] 플로우 연산
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import lecture.Example
import kotlin.random.Random

/**
 * 예제 49: 플로우와 map
 *
 * 플로우에서 map() 연산을 통해 데이터를 가공할 수 있다.
 * */
object Example49 : Example {

    override fun run() = runBlocking {
        flowSomething().map {
            println(it)
            "$it $it"
        }.collect { value ->
            println(value)
        }
    }

    fun flowSomething(): Flow<Int> = flow {
        repeat(10) {
            emit(Random.nextInt(0, 500))
            delay(500L)
        }
    }
}

/**
 * 예제 50: 플로우와 filter()
 *
 * filter() 기능을 이용해 짝수만 남겨봅시다.
 * */
object Example50 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().filter {
            it % 2 == 0
        }.collect {
            println(it)
        }
    }
}

/**
 * 예제 51: filterNot()
 * */
object Example51 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().filterNot {
            it % 2 == 0
        }.collect {
            println(it)
        }
    }
}

/**
 * 예제 52: transform() 연산자
 *
 * transform() 연산자를 이용해 조금 더 유연하게 스트림을 변형할 수 있다.
 * */
object Example52 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().transform {
            emit(it)
            emit(someCalc(it))
        }.collect {
            println(it)
        }
    }

    suspend fun someCalc(i: Int): Int {
        delay(10L)
        return i * 2
    }
}

/**
 * 예제 53: take() 연산자
 *
 * take() 연산자는 몇 개의 수행 결과만 취한다.
 * */
object Example53 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().transform {
            println("transform: $it")
            emit(it)
            emit(someCalc(it))
        }
            .take(5)
            .collect {
                println(it)
            }
    }

    suspend fun someCalc(i: Int): Int {
        delay(10L)
        return i * 2
    }
}

/**
 * 예제 54: takeWhile() 연산자
 *
 * takeWhile() 을 이용해 조건을 만족하는 동안만 값을 가져오게 할 수 있다.
 * */
object Example54 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().transform {
            println("transform: $it")
            emit(it)
            emit(someCalc(it))
        }.takeWhile {
            it < 15
        }.collect {
            println(it)
        }
    }

    suspend fun someCalc(i: Int): Int {
        delay(10L)
        return i * 2
    }
}

/**
 * 예제 55: drop() 연산자
 *
 * drop() 연산자는 처음 몇 개의 결과를 버립니다.
 * take() 가 takeWhile() 을 가지듯, drop() 도 dropWhile() 이 있다.
 * */
object Example55 : Example {

    override fun run() = runBlocking {
        (1..20).asFlow().transform {
            println("transform: $it")
            emit(it)
            emit(someCalc(it))
        }
//            .drop(5)
            .dropWhile { it < 15 }
            .collect {
                println(it)
            }
    }

    suspend fun someCalc(i: Int): Int {
        delay(10L)
        return i * 2
    }
}

/**
 * 예제 56: reduce() 연산자
 *
 * collect(), reduce(), fold(), toList(), toSet() 과 같은 연산자는 플로우를 끝내는 함수로
 * 종단 연산자(terminal operator)라고 한다.
 *
 * reduce() 는 흔히 map() 과 reduce() 로 함께 소개되는 함수형 언어의 오래된 매커니즘이다.
 * 첫 번째 값을 결과에 넣은 후, 각 값을 가져와 누진적으로 계산한다.
 * */
object Example56 : Example {

    override fun run() = runBlocking {
        val value = (1..10)
            .asFlow()
            .reduce { acc, value ->
                println("reduce: acc=$acc, value=$value")
                acc * value
            }

        println(value)
    }
}

/**
 * 예제 57: fold() 연산자
 *
 * fold() 연산자는 reduce() 와 매우 유사하지만, 초기값이 있다는 차이만 있다.
 * */
object Example57 : Example {

    override fun run() = runBlocking {
        val value = (1..10)
            .asFlow()
            .fold(10) { acc, value ->
                println("fold: acc=$acc, value=$value")
                acc + value
            }

        println(value)
    }
}

/**
 * 예제 58: count() 연산자
 *
 * count() 연산자는 술어를 만족하는 자료의 갯수를 센다.
 * 종단 연산자이다.
 * */
object Example58 : Example {

    override fun run() = runBlocking {
        val counter = (1..10)
            .asFlow()
            .count {
                it % 2 == 0
            }

        println(counter)
    }
}