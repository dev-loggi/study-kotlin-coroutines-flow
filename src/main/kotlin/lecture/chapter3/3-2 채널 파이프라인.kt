/*
* [Chapter 3] 코틀린 채널
*
* [3-2] 채널 파이프라인
* */

package lecture.chapter3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 84: 파이프라인
 *
 * 파이프라인은 일반적인 패턴이다.
 * 하나의 스트림을 프로듀서가 만들고, 다른 코루틴에서 그 스트림을 읽어 새로운 스트릠을 만드는 패턴이다.
 * */
object Example84 : Example {

    override fun run() = runBlocking {
        val numbers = produceNumbers()
        val stringNumbers = produceStringNumbers(numbers)

        repeat(5) {
            println(stringNumbers.receive())
        }

        println("완료")
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        var number = 1
        while (true) {
            send(number++)
        }
    }

    fun CoroutineScope.produceStringNumbers(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce {
        for (number in numbers) {
            send("$number!")
        }
    }
}

/**
 * 예제 85: 홀수 필터
 *
 * 파이프라인을 응용해 홀수 필터를 만들어 봅시다.
 * */
object Example85 : Example {

    override fun run() = runBlocking {
        val numbers = produceNumbers()
        val oddNumbers = filterOdd(numbers)

        repeat(10) {
            println(oddNumbers.receive())
        }

        println("완료!")
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        var number = 1
        while (true) {
            send(number++)
        }
    }

    fun CoroutineScope.filterOdd(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce {
        for (number in numbers) {
            if (number % 2 == 1) {
                send("$number!")
            }
        }
    }
}

/**
 * 예제 86: 소수 필터
 *
 * 파이프라인을 연속으로 타면서 원하는 결과를 얻을 수 있다.
 * */
object Example86 : Example {

    override fun run() {
        //solution1() // 내가 생각한 방식
        solution2() // 강의에서 해결한 방식
    }

    fun solution1() = runBlocking {
        val numbers = produceNumbers()
        val primeNumbers = filterPrime(numbers)

        repeat(10) {
            println(primeNumbers.receive())
        }

        println("완료")
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        var number = 1
        while (true) {
            send(number++)
        }
    }

    fun CoroutineScope.filterPrime(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce {
        for (number in numbers) {
            if (isPrime(number)) {
                send("$number!")
            }
        }
    }

    fun isPrime(number: Int): Boolean {
        if (number <= 1) {
            return false
        }
        var x = 2
        while (x * x <= number) {
            if (number % x == 0) {
                return false
            }
            x += 1
        }
        return true
    }

    fun solution2() = runBlocking {
        var numbers = numbersFrom(2)

        repeat(10) {
            val prime = numbers.receive()
            println(prime)

            numbers = filter(numbers, prime)
        }

        println("완료")
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.numbersFrom(start: Int): ReceiveChannel<Int> = produce {
        var x = start
        while (true) {
            send(x++)
        }
    }

    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int): ReceiveChannel<Int> = produce {
        for (i in numbers) {
            if (i % prime != 0) {
                send(i)
            }
        }
    }
}