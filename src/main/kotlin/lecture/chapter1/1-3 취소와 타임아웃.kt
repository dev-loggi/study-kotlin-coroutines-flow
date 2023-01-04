package lecture.chapter1

import kotlinx.coroutines.*

object `1-3 취소와 타임아웃` {

    /**
     * 예제 14: Job 에 대해 취소
     *
     * 명시적인 Job 에 대해 cancel() 메서드를 호출해 취소할 수 있다.
     * */
    fun example14() = runBlocking {
        doOneTwoThree()
        println("runBlocking: ${Thread.currentThread().name}")
        println("5!")
    }

    private suspend fun doOneTwoThree() = coroutineScope {
        val job1 = launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }

        val job2 = launch {
            println("launch2: ${Thread.currentThread().name}")
            delay(200L)
            println("1!")
        }

        val job3 = launch {
            println("launch3: ${Thread.currentThread().name}")
            delay(500L)
            println("2!")
        }

        println("coroutineScope: ${Thread.currentThread().name}")
        delay(800L)
        job1.cancel()
        job2.cancel()
        job3.cancel()
        println("4!")
    }

    /**
     * 예제 15: 취소 불가능한 Job
     *
     * launch(Dispatchers.Default) 는 그 다음 코드 블록을 다른 스레드에서 수행시킨다.
     *
     * 아래 예제에서는 두가지 부분만 확인하고 자세한 내용은 나중에 다룬다.
     *
     * 1. job1 이 취소든 종료든 다 끝난 이후에 "doCount Done!"을 출력하고 싶다.
     * 2. job1 이 취소가 되지 않았다.
     * */
    fun example15() = runBlocking {
        doCountForExample15()
    }

    private suspend fun doCountForExample15() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L

            while (i <= 10) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }

        delay(200L)
        job1.cancel()
        println("doCount Done!")
    }

    /**
     * 예제 16: cancel() 과 join()
     *
     * join() 은 되지만 cancel() 은 되지 않는다.
     * */
    fun example16() = runBlocking {
        doCountForExample16()
    }

    private suspend fun doCountForExample16() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L

            while (i <= 10) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }

        delay(200L)
        job1.cancel()
        job1.join()
        println("doCount Done!")
    }

    /**
     * 예제 17: cancelAndJoin()
     *
     * cancel() 을 하고 join() 하는 일은 자주 일어나는 일이기 때문에
     * cancelAndJoin() 함수를 활용하면 된다.
     *
     * but, join() 은 되지만 cancel() 은 되지 않는다.
     * */
    fun example17() = runBlocking {
        doCountForExample17()
    }

    private suspend fun doCountForExample17() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L

            while (i <= 10) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }

        delay(200L)
        job1.cancelAndJoin()
        println("doCount Done!")
    }

    /**
     * 예제 18: cancel() 가능한 코루틴
     *
     * isActive 를 호출하면 코루틴이 활성화 상태인지 확인할 수 있다.
     * */
    fun example18() = runBlocking {
        doCountForExample18()
    }

    private suspend fun doCountForExample18() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L

            while (i <= 10 && isActive) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }

        delay(200L)
        job1.cancelAndJoin()
        println("doCount Done!")
    }

    /**
     * 예제 19: finally 를 같이 사용
     *
     * launch() 에서 자원을 할당한 경우에는 어떻게 할까?
     *
     * suspend 함수들은 JobCancellationException 을 발생하기 때문에
     * 표준 try-catch-finally 구문으로 대응할 수 있다.
     * */
    fun example19() = runBlocking {
        doOneTwoThreeForExample19()
    }

    private suspend fun doOneTwoThreeForExample19() = coroutineScope {
        val job1 = launch {
            try {
                println("launch1: ${Thread.currentThread().name}")
                delay(1000L)
                println("3!")
            } finally {
                println("job1 is finishing!")
                // 파일을 닫아주는 코드
            }
        }

        val job2 = launch {
            try {
                println("launch1: ${Thread.currentThread().name}")
                delay(1000L)
                println("1!")
            } finally {
                println("job2 is finishing!")
                // 소켓을 닫아주는 코드
            }
        }

        val job3 = launch {
            try {
                println("launch3: ${Thread.currentThread().name}")
                delay(1000L)
                println("2!")
            } finally {
                println("job3 is finishing!")
                // 자원 해제...
            }
        }

        delay(800L)
        job1.cancel()
        job2.cancel()
        job3.cancel()
        println("4!")
    }

    /**
     * 예제 20: 취소 불가능한 블록
     *
     * 어떤 코드는 취소가 불가능해야 하는 경우도 있다.
     * withContext(NonCancellable) 을 이용하면 취소 불가능한 블록을 만들 수 있다.
     *
     * 위 finally 예제와 비슷하게 보이지만 조금 다르다.
     * */
    fun example20() = runBlocking {
        doOneTwoThreeForExample20()
    }

    private suspend fun doOneTwoThreeForExample20() = coroutineScope {
        val job1 = launch {
            withContext(NonCancellable) {
                println("launch1: ${Thread.currentThread().name}")
                delay(1000L)
                println("3!")
            }
            delay(1000L)
            println("job1: end")
        }

        val job2 = launch {
            withContext(NonCancellable) {
                println("launch2: ${Thread.currentThread().name}")
                delay(1000L)
                println("1!")
            }
            delay(1000L)
            println("job2: end")
        }

        val job3 = launch {
            withContext(NonCancellable) {
                println("launch3: ${Thread.currentThread().name}")
                delay(1000L)
                println("2!")
            }
            delay(1000L)
            println("job3: end")
        }

        delay(800L)
        job1.cancel()
        job2.cancel()
        job3.cancel()
        println("4!")
    }

    /**
     * 예제 21: 타임 아웃
     *
     * 일정 시간이 끝난 후에 종료하고 싶다면 withTimeout() 을 이용한다.
     * */
    fun example21() = runBlocking {
        withTimeout(500L) {
            doCountForExample21()
        }
    }

    private suspend fun doCountForExample21() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L
            while (i <= 10 && isActive) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }
    }

    /**
     * 예제 22: withTimeoutOrNull()
     *
     * 예외를 핸들하는 것은 귀찮은 일이다.
     * withTimeoutOrNull() 함수를 활용하여 Exception 이 아닌 null 을 반환해주자.
     * */
    fun example22() = runBlocking {
        val result = withTimeoutOrNull(500L) {
            doCountForExample22()
            true
        } ?: false
        println("result=$result")
    }

    private suspend fun doCountForExample22() = coroutineScope {
        val job1 = launch(Dispatchers.Default) {
            var i = 1
            var nextTime = System.currentTimeMillis() + 100L
            while (i <= 10 && isActive) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= nextTime) {
                    println(i)
                    nextTime = currentTime + 100L
                    i += 1
                }
            }
        }
    }
}