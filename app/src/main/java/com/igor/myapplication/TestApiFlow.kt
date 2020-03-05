package com.igor.myapplication

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TestApiFlow {

    private val repository = Repository()

    private fun fakeApiFlow() {
        CoroutineScope(Dispatchers.Default).launch {
            val users: Flow<User> = repository.getAllByFlow()
            users.collect { user ->
                launch(Dispatchers.Main) {
                    Log.d(MainActivity.FLOW, user.age.toString())
                }
            }
        }
    }
}