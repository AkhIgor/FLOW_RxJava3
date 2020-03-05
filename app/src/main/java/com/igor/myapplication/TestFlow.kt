package com.igor.myapplication

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestFlow {

    fun getAllByFlow(): Flow<User> {
        return flow {
            for (i in 1..10_000) {
                val user = User("Igor", i)
                emit(user)
            }
        }
    }
}