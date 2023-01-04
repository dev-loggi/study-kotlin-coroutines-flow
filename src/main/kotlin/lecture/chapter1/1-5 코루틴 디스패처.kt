package lecture.chapter1

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object `1-5 코루틴 디스패처` {

    /**
     * 예제 27: 코루틴 디스패처
     *
     * 코루틴의 여러 디스패처 Default, IO, Unconfined, newSingleThreadContext 를 사용해보자.
     *
     * [Dispatchers.Default]
     * 코어 수에 비례하는 스레드 풀에서 수행한다.(복잡한 연산)
     *
     * [Dispatchers.IO]
     * IO 는 코어 수보다 훨씬 많은 스레드를 가지는 스레드 풀이다.
     * IO 작업은 CPU 를 덜 소모하기 때문이다.
     * File I/O, Network 는 CPU 를 소모하지 않는다(?)
     *
     * [Dispatchers.Unconfined]
     * 어디에도 속하지 않는다.
     * 아래 예제에서는 부모 스레드에서 수행될 것이다.
     * but, 어느 스레드에서 실행될지 예측할 수 없다.
     *
     * [newSingleThreadContext]
     * 항상 새로운 스레드를 만든다
     * */
    fun example27() = runBlocking {
        launch {
            println("부모의 컨텍스트: ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) {
            println("Default 컨텍스트: ${Thread.currentThread().name}")
        }
        launch(Dispatchers.IO) {
            println("IO 컨텍스트: ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) {
            println("Unconfined 컨텍스트: ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("loggi")) {
            println("newSingleThreadContext: ${Thread.currentThread().name}")
        }
    }

    /**
     * 예제 28: async() 에서 코루틴 디스패처 사용
     *
     * launch() 외에 async(), withContext() 등의 코루틴 빌더에서도 디스패처를 사용할 수 있다.
     * */
    fun example28() = runBlocking<Unit> {
        async {
            println("부모의 컨텍스트: ${Thread.currentThread().name}")
        }
        async(Dispatchers.Default) {
            println("Default 컨텍스트: ${Thread.currentThread().name}")
        }
        async(Dispatchers.IO) {
            println("IO 컨텍스트: ${Thread.currentThread().name}")
        }
        async(Dispatchers.Unconfined) {
            println("Unconfined 컨텍스트: ${Thread.currentThread().name}")
        }
        async(newSingleThreadContext("loggi")) {
            println("newSingleThreadContext: ${Thread.currentThread().name}")
        }
    }

    /**
     * 예제 29: Unconfined 디스패처 테스트
     *
     * Confined 는 처음에는 부모 스레드에서 수행된다.
     * 그러나 한 번 중단점(suspension point)에 오면 바뀌게 된다.
     * 그래서 가능하면 확실한 디스패처를 사용하자.
     * */
    fun example29() = runBlocking<Unit> {
        async(Dispatchers.Unconfined) {
            println("Unconfined: ${Thread.currentThread().name}")
            delay(1000L) // suspension point
            println("Unconfined: ${Thread.currentThread().name}")
            delay(1000L) // suspension point
            println("Unconfined: ${Thread.currentThread().name}")
        }
    }

    /**
     * 예제 30: 부모가 있는 Job 과 없는 Job
     *
     * CoroutineScope, CoroutineContext 는 구조화 되어 있고 부모에게 계층적으로 되어 있다.
     * CoroutineContext 의 Job 역시 부모에게 의존적이다.
     * 부모를 캔슬했을 때 영향을 확인해보자.
     * */
    fun example30() = runBlocking {// 조부모
        val job = launch { // 부모
            launch(Job()) { // 부모 없는 자식
                println(coroutineContext[Job])
                println("launch1: ${Thread.currentThread().name}")
                delay(1000L)
                println("3!")
            }

            launch { // 부모 있는 자식
                println(coroutineContext[Job])
                println("launch2: ${Thread.currentThread().name}")
                delay(1000L)
                println("1!")
            }
        }

        delay(500L)
        job.cancelAndJoin() // launch1 만 캔슬된다
        delay(1000L) // launch2 를 기다려주기 위함
    }

    /**
     * 예제 31: 부모의 마음
     *
     * 구조화되어 계층화된 코루틴은 자식들의 실행을 지켜볼까요?
     * */
    fun example31() = runBlocking {
        val elapsed = measureTimeMillis {
            val job = launch {
                launch {
                    println("launch1: ${Thread.currentThread().name}")
                    delay(5000L)
                    println("launch1 end!")
                }
                launch {
                    println("launch2: ${Thread.currentThread().name}")
                    delay(100L)
                    println("launch2 end!")
                }
            }
            job.join()
        }
        println(elapsed)
    }

    /**
     * 예제 32: 코루틴 엘리먼트 결합
     *
     * 여러 코루틴 엘리먼트를 한 번에 사용할 수 있다.
     * `+` 연산으로 엘리먼트를 합치면 된다.
     * 합쳐진 엘리먼트들은 coroutineContext[xxx] 으로 조회할 수 있다.
     *
     * 코루틴 컨텍스트는 기본적으로 부모 컨텍스트에 자식 컨텍스트 엘리먼트를 결합하여 사용된다.
     * */
    @OptIn(ExperimentalStdlibApi::class)
    fun example32() = runBlocking {
        launch {
            launch(Dispatchers.IO + CoroutineName("launch1")) {
                println("launch1: ${Thread.currentThread().name}")
                println(coroutineContext[CoroutineDispatcher])
                println(coroutineContext[CoroutineName])
                delay(5000L)
            }
            launch(Dispatchers.Default + CoroutineName("launch2")) {
                println("launch2: ${Thread.currentThread().name}")
                println(coroutineContext[CoroutineDispatcher])
                println(coroutineContext[CoroutineName])
                delay(100L)
            }
        }
    }
}