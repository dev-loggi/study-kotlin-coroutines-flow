package lecture.chapter1

import kotlinx.coroutines.*

object `1-2 Job, 구조화된 동시성` {

    /**
     * 예제 10: suspend 함수에서 코루틴 빌더 호출
     *
     * runBlocking() 외의 코루틴 빌더,
     * 즉 launch() 함수는 코루틴 스코프 내에서 호출이 가능하다.
     * */
    fun example10() = runBlocking {
        doOneTwoThree() // suspension point
        println("runBlocking: ${Thread.currentThread().name}")
        delay(100L) // suspension point
        println("5!")
    }

    private suspend fun doOneTwoThree() {
        // 동작하지 않는 코드
        /*
        launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L) // suspension point
            println("3!")
        }
        launch {
            println("launch2: ${Thread.currentThread().name}")
            println("1!")
        }
        launch {
            println("launch3: ${Thread.currentThread().name}")
            delay(500L) // suspension point
            println("2!")
        }
        */
        println("4!")
    }

    /**
     * 예제 11: 코루틴 스코프(Coroutine Scope)
     *
     * 코루틴 스코프를 만드는 다른 방법은 [스코프 빌더]를 이용한 것이다.
     * 위 예제 10 코드를 동작하게 만들어 봅시다.
     *
     * withContext(), runBlocking() 은 현재 스레드를 멈추게 만들지만,
     * coroutineScope() 은 스레드를 멈추지 않고, 호출한 쪽이 suspend 되고 시간이 되면 다시 활동한다.
     * */
    fun example11() = runBlocking {
        doOneTwoThree2()
        println("runBlocking: ${Thread.currentThread().name}")
        delay(100L)
        println("5!")
    }

    private suspend fun doOneTwoThree2() = coroutineScope {
        launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }
        launch {
            println("launch2: ${Thread.currentThread().name}")
            println("1!")
        }
        launch {
            println("launch3: ${Thread.currentThread().name}")
            delay(500L)
            println("2!")
        }
        println("4!")
    }

    /**
     * 예제 12: Job 을 이용한 제어
     *
     * 코루틴 빌더 launch()는 Job 객체를 반환하며,
     * 이를 통해 종료될 때까지 기다릴 수 있다.
     *
     * Job.join(): Suspends the coroutine until this job is complete.
     * */
    fun example12() = runBlocking {
        doOneTwoThree3()
        println("runBlocking: ${Thread.currentThread().name}")
        println("5!")
    }

    private suspend fun doOneTwoThree3() = coroutineScope {
        val job = launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }
        job.join() // suspension point

        launch {
            println("launch2: ${Thread.currentThread().name}")
            println("1!")
        }
        launch {
            println("launch3: ${Thread.currentThread().name}")
            delay(500L)
            println("2!")
        }
        println("4!")
    }

    /**
     * 예제 13: 가벼운 코루틴
     *
     * 코루틴은 협력적으로 동작하기 때문에 코루틴을 만드는 것이 큰 비용이 들지 않는다.
     * 10만개의 간단한 일을 하는 코루틴도 큰 부담이 아니다.
     * */
    fun example13() = runBlocking {
        makeManyCoroutines()
        println("runBlocking: ${Thread.currentThread().name}")
        println("3!")
    }

    private suspend fun makeManyCoroutines() = coroutineScope {
        repeat(1_000_000) {
            launch {
                println("launch: $it")
            }
        }
        println("1!")
    }
}