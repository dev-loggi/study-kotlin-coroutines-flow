package lecture.chapter1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object `1-1 스코프빌더` {

    /**
     * 예제 1: 간단한 코루틴
     *
     * runBlocking()
     * 코루틴을 만드는 가장 간단한 함수
     * (코루틴을 만드는 함수를 코루틴 빌더라고 한다)
     * 코루틴을 만들고 코드 블록의 수행이 끝날 때까지 다음 코드를 수행하지 못하게 막는다.
     * */
    fun example1() = runBlocking {
        println(Thread.currentThread())
        println(Thread.currentThread().name)
    }

    /**
     * 예제 2: 코루틴 빌더의 수신 객체
     *
     * 코루틴 빌더의 수신 객체(Receiver)는 코루틴(코루틴 스코프)이다.
     *
     * BlockingCoroutine 은 CoroutineScope 의 자식이다.
     * 코루틴을 쓰는 모든 곳에는 CoroutineScope 가 있다고 생각하면 된다.
     *
     * [코루틴의 시작은 코루틴 스코프다]
     * */
    fun example2() = runBlocking {
        println(this)
        println(Thread.currentThread().name)
    }

    /**
     * 예제 3: 코루틴 컨텍스트
     *
     * CoroutineScope 는 코루틴을 제대로 처리하기 위한 정보, 즉 CoroutineContext 를 가지고 있다.
     * */
    fun example3() = runBlocking {
        println(coroutineContext)
        println(Thread.currentThread().name)
    }

    /**
     * 예제 4: launch() 코루틴 빌더
     *
     * launch() 도 코루틴 빌더이다
     * 새로운 코루틴 스코프를 생성한다.
     * "할 수 있다면 다른 코루틴 코드를 같이 수행"하는 코루틴 빌더이다.
     * "다른 코루틴 코드와 함께 쓰기 위해서 보통 사용"
     * */
    fun example4() = runBlocking {
        launch {
            println("launch: ${Thread.currentThread().name}")
            println("World!1")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        println("Hello")
    }

    /**
     * 예제 5: delay 함수
     *
     * Delays coroutine for a given time without blocking a thread and resumes it after a specified time.
     *
     * 스레드를 멈추는 것이 아닌, 코루틴 스코프를 대기 상태로 만듦
     * 즉, 다른 코루틴은 동작할 수 있음(양보)
     * */
    fun example5() = runBlocking {
        launch {
            println("launch: ${Thread.currentThread().name}")
            println("World!")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        delay(500L)
        println("Hello")
    }

    /**
     * 예제 6: 코루틴 내에서 sleep
     *
     * delay() 와는 다르게 스레드를 양보하지 않는다.
     * */
    fun example6() = runBlocking {
        launch {
            println("launch: ${Thread.currentThread().name}")
            println("World!")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        Thread.sleep(500)
        println("Hello")
    }

    /**
     * 예제 7: 한번에 여러 launch()
     *
     * 1, 2, 3을 순서대로 수행시켜봅시다.
     * */
    fun example7() = runBlocking {
        launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L) // suspension point
            println("3!")
        }
        launch {
            println("launch2: ${Thread.currentThread().name}")
            println("1!")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        delay(500L) // suspension point (중단점)
        println("2!")
    }

    /**
     * 예제 8: 상위 코루틴은 하위 코루틴을 끝까지 책임진다
     *
     * runBlocking()안에 두 launch()가 계층화 된 구조에서,
     * runBlocking()은 내부 launch()가 모두 끝나기 전까지 종료되지 않는다.
     *
     * (팁, 상위 코루틴을 cancel 하면 하위 코루틴도 같이 cancel 된다)
     * */
    fun example8() {
        runBlocking { // 계층적, 구조적
            launch {
                println("launch1: ${Thread.currentThread().name}")
                delay(1000L)
                println("3!")
            }
            launch {
                println("launch2: ${Thread.currentThread().name}")
                println("1!")
            }
            println("runBlocking: ${Thread.currentThread().name}")
            delay(500L)
            println("2!")
        }
        println("4!")
    }

    /**
     * 예제 9: suspend 함수
     *
     * delay(), launch() 등의 함수는 코루틴 내에서만 호출 할 수 있다.
     * 그럼 이 함수들을 포함한 코드들을 함수로 분리해보자.
     * 정답은 suspend 함수이다.
     * */
    fun example9() = runBlocking {
        launch {
            doThree()
        }
        launch {
            doOne()
        }
        doTwo()
    }

    private suspend fun doThree() {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    private suspend fun doOne() {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    private suspend fun doTwo() {
        println("runBlocking: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }
}