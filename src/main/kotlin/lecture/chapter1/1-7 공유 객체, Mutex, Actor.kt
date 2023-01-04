package lecture.chapter1

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

object `1-7 공유 객체, Mutex, Actor` {

    /**
     * 예제 39: 공유 객체 문제
     *
     * withContext() 는 수행이 완료될 때까지 기다리는 코루틴 빌더이다.
     * runBlocking() 와의 차이점은 스레드를 blocking/nonblocking 이다.
     *
     * 아래 예제는 불행히도 counter 값이 항상 100,000 이 되는 것이 아니다.
     * Dispatchers.Default 에 의해 코루틴이 어떻게 할당되느냐에 따라 값이 달라진다.
     * */
    private var counter39 = 0

    fun example39() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun39 {
                counter39 += 1
            }
        }
        println("counter=$counter39")
    }

    private suspend fun massiveRun39(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }

    /**
     * 예제 40: volatile 적용해보기
     *
     * volatile 은 가시성 문제만 해결할 뿐,
     * 동시에 읽고 수정해서 생기는 문제를 해결하지 못한다.
     * */
    @Volatile
    private var counter40 = 0

    fun example40() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun40 {
                counter40 += 1
            }
        }
        println("counter=$counter40")
    }

    private suspend fun massiveRun40(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }

    /**
     * 예제 41: Thread safety 한 자료구조 사용하기
     *
     * [AtomicInteger] 와 같은 Thread safety 한 자료구조를 사용한다.
     * (단, 모든 경우에서 AtomicInteger 가 정답인 것은 아니다.)
     * */
    private val counter41 = AtomicInteger()

    fun example41() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun41 {
                counter41.incrementAndGet()
            }
        }
        println("counter=$counter41")
    }

    private suspend fun massiveRun41(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }

    /**
     * 예제 42: 스레드 한정
     *
     * [newSingleThreadContext] 를 이용해서 특정한 스레드를 만들고 해당 스레드를 사용할 수 있다.
     * 얼만큼 한정지을 것인지는 자유롭게 정해보자.
     * */
    @OptIn(DelicateCoroutinesApi::class)
    private val counterContext42 = newSingleThreadContext("CounterContext")
    private var counter42 = 0

    fun example42() = runBlocking {
        withContext(counterContext42) {
            massiveRun42 {
                counter42 += 1
            }
        }
        // 혹은
//        withContext(Dispatchers.Default) {
//            massiveRun42 {
//                withContext(counterContext42) {
//                    counter42 += 1
//                }
//            }
//        }
        println("counter=$counter42")
    }

    private suspend fun massiveRun42(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }

    /**
     * 예제 43: 뮤텍스(Mutex)
     *
     * 뮤텍스는 상호배제(Mutual exclusion)의 줄임말이다.
     *
     * 공유 상태를 수정할 때, 임계 영역(critical section)을 이용하게 하며
     * 임계 영역을 동시에 접근하는 것을 허용하지 않는다.
     * */
    private val mutex43 = Mutex()
    private var counter43 = 0

    fun example43() = runBlocking {
        withContext(counterContext42) {
            massiveRun43 {
                mutex43.withLock {
                    counter43 += 1
                }
            }
        }
        println("counter=$counter43")
    }

    private suspend fun massiveRun43(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }

    /**
     * 예제 44: 액터(Actor)
     *
     * 액터는 1973년에 칼 휴이트가 만든 개념으로
     * 액터가 독점적으로 자료를 가지며
     * 그 자료를 다른 코루틴과 공유하지 않고 액터를 통해서만 접근하게 만든다.
     * (1973년이지만 최신 기술로 볼 수 있다)
     *
     * 채널은 송신 측에서 값을 send 할 수 있고, 수신 측에서 receive 할 수 있는 도구이다.
     * (채널에 대해 더 상세한 내용은 3부와 4부에서 다루도록 한다.)
     * */
    sealed class CounterMsg44
    object IncCounter44 : CounterMsg44()
    class GetCounter44(val response: CompletableDeferred<Int>) : CounterMsg44()

    fun example44() = runBlocking {
        val counter = counterActor44()

        withContext(Dispatchers.Default) {
            massiveRun44 {
                counter.send(IncCounter44) // suspension point
            }
        }

        val response = CompletableDeferred<Int>()
        counter.send(GetCounter44(response)) // suspension point
        println("counter=${response.await()}") // suspension point
        counter.close()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.counterActor44() = actor<CounterMsg44> {
        var counter = 0 // 액터 안에 상태를 캡슐화해두고 다른 코루틴이 접근하지 못하게 한다

        for (msg in channel) { // 외부에서 보내는 것은 채널을 통해서만 받을 수 있다(receive)
            when (msg) {
                is IncCounter44 -> counter += 1 // 증가시키는 신호
                is GetCounter44 -> msg.response.complete(counter) // 현재 상태 반환
            }
        }
    }

    private suspend fun massiveRun44(action: suspend () -> Unit) {
        val n = 100 // 시작할 코루틴의 갯수
        val k = 1000 // 코루틴 내에서 반복할 횟수
        val elapsed = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("$elapsed ms 동안 ${n * k}개의 액션을 수행하였습니다.")
    }
}