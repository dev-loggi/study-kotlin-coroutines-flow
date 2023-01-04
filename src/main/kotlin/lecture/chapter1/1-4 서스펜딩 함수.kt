package lecture.chapter1

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object `1-4 서스펜딩 함수` {

    /**
     * 예제 23: suspend 함수들의 순차적인 수행
     *
     * 순차적으로 suspend 함수를 먼저 수행시켜본다.
     * */
    fun example23() = runBlocking {
        val elapsedTime = measureTimeMillis {
            val value1 = getRandom1ForExample23()
            val value2 = getRandom2ForExample23()
            println("$value1 + $value2 = ${value1 + value2}")
        }
        println("elapsedTime=$elapsedTime")
    }

    private suspend fun getRandom1ForExample23(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    private suspend fun getRandom2ForExample23(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    /**
     * 예제 24: async 를 이용해 동시 수행하기
     *
     * async 키워드를 이용하면 동시에 다른 블록을 수행할 수 있다.
     * launch() 와 비슷하게 보이지만 수행 결과를 await 키워드를 통해 받을 수 있다는 차이가 있다.
     *
     * 즉, 결과를 받아야 한다면 async, 결과를 받지 않아도 되면 launch 를 선택한다.
     *
     * await 키워드를 만나면 async 블록의 수행이 끝났는지 확인하고,
     * 아직 끝나지 않았다면 suspend 되었다가 다시 깨어나고 반환 값을 받아오게 된다.
     * */
    fun example24() = runBlocking {
        val elapsedTime = measureTimeMillis {
            val value1 = async { getRandom1ForExample24() }
            val value2 = async { getRandom2ForExample24() }

            println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
        }
        println("elapsedTime=$elapsedTime")
    }

    private suspend fun getRandom1ForExample24(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    private suspend fun getRandom2ForExample24(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    /**
     * 예제 25: async 게으르게 사용하기
     *
     * async 키워드를 사용하는 순간 코드 블록이 수행을 준비하는데,
     * async(start = CoroutineStart.Lazy) 를 사용하면 원하는 순간에 수행 준비를 하게 할 수 있다.
     * 이후 start() 메서드가 호출되면 수행 준비가 된다.
     *
     * 일반적으로 많이 쓰이지는 않는다.
     * */
    fun example25() = runBlocking {
        val elapsedTime = measureTimeMillis {
            val value1 = async(start = CoroutineStart.LAZY) { getRandom1ForExample25() }
            val value2 = async(start = CoroutineStart.LAZY) { getRandom2ForExample25() }

            // 큐에 수행 예약을 한다.
            value1.start()
            value2.start()

            println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
        }
        println("elapsedTime=$elapsedTime")
    }

    private suspend fun getRandom1ForExample25(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    private suspend fun getRandom2ForExample25(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    /**
     * 예제 26: async 를 사용한 구조적인 동시성
     *
     * 예외가 발생하면 부모, 형제 코루틴 스코프가 모두 취소된다.
     * */
    fun example26() = runBlocking {
        try {
            doSomethingForExample26()
        } catch (e: IllegalStateException) {
            println("doSomething failed: $e")
        }
    }

    private suspend fun doSomethingForExample26() = coroutineScope {
        val value1 = async { getRandom1ForExample26() }
        val value2 = async { getRandom2ForExample26() }

        try {
            println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
        } finally {
            println("doSomething is cancelled")
        }
    }

    private suspend fun getRandom1ForExample26(): Int {
        try {
            delay(1000L)
            return Random.nextInt(0, 500)
        } finally {
            println("getRandom1 is cancelled")
        }
    }

    private suspend fun getRandom2ForExample26(): Int {
        delay(500L)
        throw IllegalStateException()
    }


}