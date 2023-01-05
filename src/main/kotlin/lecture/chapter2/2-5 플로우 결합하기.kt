/*
* [Chapter 2] 비동기 데이터 스트림. 플로우.
*
* [2-5] 플로우 결합하기
* */

package lecture.chapter2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking
import lecture.Example

/**
 * 예제 66: zip() 으로 묶기
 *
 * zip() 은 양쪽의 데이터를 하나로 묶어 새로운 데이터를 만든다.
 * */
object Example66 : Example {

    override fun run() = runBlocking {
        val nums = flowOf(1, 2, 3, 4, 5).onEach { delay(100) }
        val strs = flowOf("일", "이", "삼", "사", "오").onEach { delay(500) }

        nums.zip(strs) { a, b -> "$a 은(는) $b"}
            .collect { println(it) }
    }
}

/**
 * 예제 67: combine() 으로 묶기
 *
 * combine() 은 양쪽의 데이터를 같은 시점에 묶지 않고,
 * 한 쪽이 갱신되면 새로 묶어 데이터를 만든다.
 * */
object Example67 : Example {

    override fun run() = runBlocking {
        val nums = flowOf(1, 2, 3, 4, 5).onEach { delay(100) }
        val strs = flowOf("일", "이", "삼", "사", "오").onEach { delay(200) }

        nums.combine(strs) { a, b -> "$a 은(는) $b" }
            .collect { println(it) }
    }
}

