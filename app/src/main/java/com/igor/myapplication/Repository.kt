package com.igor.myapplication

import android.util.Log
import com.igor.myapplication.MainActivity.Companion.THREAD
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

    suspend fun getAllByFlow(): Flow<User> {
        return flow {
            for (i in 1..10_000) {
                val user = User("Igor", i)
                Log.d(THREAD, Thread.currentThread().name)
                emit(user)
            }
        }
    }

    fun s(s: String) {
        print("hello, $s sw")
    }
}