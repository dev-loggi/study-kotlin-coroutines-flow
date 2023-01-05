/*
* [Chapter 1] 코루틴과 동시성 프로그래밍
*
* [1-6] CEH 와 슈퍼바이저 잡
* */

package lecture.chapter1

import kotlinx.coroutines.*
import lecture.Example
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

/**
 * 예제 33: GlobalScope
 *
 * 어디에도 속하지 않지만 원래부터 존재하는 전역 [GlobalScope] 가 있다.
 * 전역 스코프를 이용하면 코루틴을 쉽게 수행할 수 있다.
 *
 * [GlobalScope] 는 어떤 계층에도 속하지 않고 영원히 동작한다는 문제가 있다.
 * 프로그래밍에서 전역 객체를 잘 사용하지 않는 것처럼 [GlobalScope] 도 잘 사용하지 않는다.
 * */
object Example33 : Example {

    override fun run() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            launch { printRandom() }
        }
        Thread.sleep(1000L)
    }

    private suspend fun printRandom() {
        delay(500L)
        println(Random.nextInt(0, 500))
    }
}

/**
 * 예제 34: CoroutineScope
 *
 * [GlobalScope] 보다 권장되는 형식은 [CoroutineScope] 를 사용하는 것이다.
 * [CoroutineScope] 인자로 [CoroutineContext] 를 받는데
 * 코루틴 엘리먼트를 하나만 넣어도 되고, 엘리먼트를 합쳐 코루틴 컨텍스트를 만들어도 된다.
 * */
object Example34 : Example {

    override fun run() {
        // val scope = CoroutineScope(Dispatchers.Default + CoroutineName("Scope"))
        val scope = CoroutineScope(Dispatchers.Default)
        val job = scope.launch(Dispatchers.IO) {
            launch { printRandom() }
        }
        Thread.sleep(1000L)
    }

    private suspend fun printRandom() {
        delay(500L)
        println(Random.nextInt(0, 500))
    }
}

/**
 * 예제 35: CEH (Coroutine Exception Handler)
 *
 * 예외를 가장 체계적으로 다루는 방법은 CEH 를 사용하는 것이다.
 *
 * [CoroutineExceptionHandler] 를 이용해서 우리만의 CEH 를 만든 다음,
 * 상위 코루틴 빌더의 컨텍스트에 등록합니다.
 * */
object Example35 : Example {

    override fun run() = runBlocking {
        // 일반적으로 람다의 첫번째 인자인 CoroutineContext 는 사용하지 않기 때문에 _ 로 남겨둔다.
        val ceh = CoroutineExceptionHandler { _, exception ->
            println("Something happened: $exception")
        }

        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch(ceh) {
            launch { printRandom1() }
            launch { printRandom2() }
        }

        job.join()
    }

    private suspend fun printRandom1() {
        delay(1000L)
        println(Random.nextInt(0, 500))
    }

    private suspend fun printRandom2() {
        delay(500L)
        throw ArithmeticException()
    }
}

/**
 * 예제 36: runBlocking() 과 CEH
 *
 * runBlocking() 에서는 CEH 를 사용할 수 없다.
 * runBlocking() 은 자식이 예외로 종료되면 항상 종료되고 CEH 를 호출하지 않는다.
 * */
object Example36 : Example {

    private val ceh = CoroutineExceptionHandler { _, exception ->
        println("Something happened: $exception")
    }

    override fun run() = runBlocking {
        val job = launch(ceh) {
            val a = async { printRandom1() }
            val b = async { printRandom2() }
            println(a.await())
            println(b.await())
        }
        job.join()
    }

    private suspend fun printRandom1(): Int {
        delay(1000L)
        return Random.nextInt(0, 500)
    }

    private suspend fun printRandom2(): Int {
        delay(500L)
        throw ArithmeticException()
    }
}

/**
 * 예제 37: SupervisorJob
 *
 * 슈퍼바이저 잡은 예외에 의한 취소를 아래쪽으로 내려가게 한다.
 * 즉, 자식만 취소시킨다.
 *
 * joinAll() 은 복수 개의 Job 에 대해 join() 을 수행하여 완전히 종료될 때까지 기다린다.
 * */
object Example37 : Example {

    private val ceh = CoroutineExceptionHandler { _, exception ->
        println("Something happened: $exception")
    }

    override fun run() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)
        val job1 = scope.launch { printRandom1() }
        val job2 = scope.launch { printRandom2() }

        joinAll(job1, job2)
    }

    private suspend fun printRandom1() {
        delay(1000L)
        println(Random.nextInt(0, 500))
    }

    private suspend fun printRandom2() {
        delay(500L)
        throw ArithmeticException()
    }
}

/**
 * 예제 38: SupervisorScope
 *
 * 코루틴 스코프와 슈퍼바이저 잡을 합친듯 한 SupervisorScope 가 있다.
 *
 * 단, SupervisorScope 사용시 주의점은 무조건 자식 수준에서 예외를 핸들링 해야한다.
 * 그 이유는 자식의 실패가 부모에게 전달되지 않기 때문이다.
 * */
object Example38 : Example {

    private val ceh = CoroutineExceptionHandler { _, exception ->
        println("Something happened: $exception")
    }

    override fun run() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch {
            supervisorFunc()
        }
        job.join()
    }

    private suspend fun supervisorFunc() = supervisorScope {
        launch { printRandom1() }
        launch(ceh) { printRandom2() }
    }

    private suspend fun printRandom1() {
        delay(1000L)
        println(Random.nextInt(0, 500))
    }

    private suspend fun printRandom2() {
        delay(500L)
        throw ArithmeticException()
    }
}