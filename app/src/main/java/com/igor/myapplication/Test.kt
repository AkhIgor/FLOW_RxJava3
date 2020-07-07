package com.igor.myapplication

import kotlinx.coroutines.delay

class Test {

    private suspend fun coroutine() {
        for (time in 1..100L) {
            println("Coroutine started!")
            delay(time)
            println("Coroutine finished!")
        }
    }
}