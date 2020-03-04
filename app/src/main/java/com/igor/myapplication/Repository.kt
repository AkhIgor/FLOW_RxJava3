package com.igor.myapplication

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository {

    fun getAllByRx(): Observable<User> {
        return Observable.create { emitter ->
            for (i in 1..10_000) {
                val user = User("Igor", i)
                emitter.onNext(user)
            }
            emitter.onComplete()
        }
    }

    fun getAllByFlow(): Flow<User> {
        return flow {
            for (i in 1..10_000) {
                val user = User("Igor", i)
                emit(user)
            }
        }
    }
}