package lecture

import lecture.chapter1.*
import lecture.chapter2.*
import lecture.chapter3.*

/** Kotlin Coroutines & Flow */
object Lecture {

    /** 1부. 코루틴과 동시성 프로그래밍 */
    object Chapter1 {

        /** 1-1 스코프빌더 */
        object ByOne {
            val example1: Example get() = Example1
            val example2: Example get() = Example2
            val example3: Example get() = Example3
            val example4: Example get() = Example4
            val example5: Example get() = Example5
            val example6: Example get() = Example6
            val example7: Example get() = Example7
            val example8: Example get() = Example8
            val example9: Example get() = Example9
        }

        /** 1-2 Job, 구조화된 동시성 */
        object ByTwo {
            val example10: Example get() = Example10
            val example11: Example get() = Example11
            val example12: Example get() = Example12
            val example13: Example get() = Example13
        }

        /** 1-3 취소와 타임아웃 */
        object ByThree {
            val example14: Example get() = Example14
            val example15: Example get() = Example15
            val example16: Example get() = Example16
            val example17: Example get() = Example17
            val example18: Example get() = Example18
            val example19: Example get() = Example19
            val example20: Example get() = Example20
            val example21: Example get() = Example21
            val example22: Example get() = Example22
        }

        /** 1-4 서스펜딩 함수 */
        object ByFour {
            val example23: Example get() = Example23
            val example24: Example get() = Example24
            val example25: Example get() = Example25
            val example26: Example get() = Example26
        }

        /** 1-5 코루틴 디스페처 */
        object ByFive {
            val example27: Example get() = Example27
            val example28: Example get() = Example28
            val example29: Example get() = Example29
            val example30: Example get() = Example30
            val example31: Example get() = Example31
            val example32: Example get() = Example32
        }

        /** 1-6 CEH 와 슈퍼바이저 잡 */
        object BySix {
            val example33: Example get() = Example33
            val example34: Example get() = Example34
            val example35: Example get() = Example35
            val example36: Example get() = Example36
            val example37: Example get() = Example37
            val example38: Example get() = Example38
        }

        /** 1-7 공유 객체, Mutex, Actor */
        object BySeven {
            val example39: Example get() = Example39
            val example40: Example get() = Example40
            val example41: Example get() = Example41
            val example42: Example get() = Example42
            val example43: Example get() = Example43
            val example44: Example get() = Example44
        }
    }

    /** 2부. 비동기 데이터 스트림. 플로우. */
    object Chapter2 {

        /** 2-1 플로우 기초 */
        object ByOne {
            val example45: Example get() = Example45
            val example46: Example get() = Example46
            val example47: Example get() = Example47
            val example48: Example get() = Example48
        }

        /** 2-2 플로우 연산 */
        object ByTwo {
            val example49: Example get() = Example49
            val example50: Example get() = Example50
            val example51: Example get() = Example51
            val example52: Example get() = Example52
            val example53: Example get() = Example53
            val example54: Example get() = Example54
            val example55: Example get() = Example55
            val example56: Example get() = Example56
            val example57: Example get() = Example57
            val example58: Example get() = Example58
        }

        /** 2-3 플로우 컨텍스트 */
        object ByThree {
            val example59: Example get() = Example59
            val example60: Example get() = Example60
            val example61: Example get() = Example61
        }

        /** 2-4 플로우 버퍼링 */
        object ByFour {
            val example62: Example get() = Example62
            val example63: Example get() = Example63
            val example64: Example get() = Example64
            val example65: Example get() = Example65
        }

        /** 2-5 플로우 결합하기 */
        object ByFive {
            val example66: Example get() = Example66
            val example67: Example get() = Example67
        }
        
        /** 2-6 플로우 플래트닝하기 */
        object BySix {
            val example68: Example get() = Example68
            val example69: Example get() = Example69
            val example70: Example get() = Example60
        }

        /** 2-7 플로우 예외처리하기 */
        object BySeven {
            val example71: Example get() = Example71
            val example72: Example get() = Example72
            val example73: Example get() = Example73
            val example74: Example get() = Example74
        }

        /** 2-8 플로우 완료처리하기 */
        object ByEight {
            val example75: Example get() = Example75
            val example76: Example get() = Example76
            val example77: Example get() = Example77
        }

        /** 2-9 플로우 런칭 */
        object ByNine {
            val example78: Example get() = Example78
            val example79: Example get() = Example79
        }
    }

    /** 3부. 코틀린 채널 */
    object Chapter3 {

        /** 3-1 채널 기초 */
        object ByOne {
            val example80: Example get() = Example80
            val example81: Example get() = Example81
            val example82: Example get() = Example82
            val example83: Example get() = Example83
        }

        /** 3-2 채널 파이프라인 */
        object ByTwo {
            val example84: Example get() = Example84
            val example85: Example get() = Example85
            val example86: Example get() = Example86
        }

        /** 3-3 채널 팬아웃, 팬인 */
        object ByThree {
            val example87: Example get() = Example87
            val example88: Example get() = Example88
            val example89: Example get() = Example89
            val example90: Example get() = Example90
        }

        /** 3-4 채널 버퍼링 */
        object ByFour {
            val example91: Example get() = Example91
            val example92: Example get() = Example92
            val example93: Example get() = Example93
        }
    }
}