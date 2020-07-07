package com.igor.myapplication
//
//import android.util.Log
//import android.widget.TextView
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
//import io.reactivex.rxjava3.schedulers.Schedulers
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.flow.map
//import kotlin.time.ExperimentalTime
//import kotlin.time.measureTime
//
//@ExperimentalCoroutinesApi
//@ExperimentalTime
//private fun fakeApiFlow() {
//    CoroutineScope(Dispatchers.Default).launch {
//        val users: Flow<User> = repository.getAllByFlow()
//            .filter { user -> user.age % 2 == 0 }
//            .map { user ->
//                user.age = user.age / 2
//                return@map user
//            }
//        val time = measureTime {
//            users.collect { user ->
//                launch(Dispatchers.Main) {
//                    Log.d(MainActivity.FLOW, user.age.toString())
//                    Log.d(MainActivity.THREAD, Thread.currentThread().name)
//                    printResult(user.age.toString())
//                }
//            }
//        }
//        Log.d(MainActivity.TIME, time.toString())
//    }
////        CoroutineScope(Main).launch {
////            repository.getAllByFlow()
////                .flowOn(IO)
////                .map { user ->
////                    Log.d(THREAD, Thread.currentThread().name)
////                    user.age = user.age / 2
////                    return@map user
////                }
////                .flowOn(Default)
////                .collect { user ->
////                    Log.d(FLOW, user.age.toString())
////                    Log.d(THREAD, Thread.currentThread().name)
////                    printResult(user.age.toString())
////                }
////        }
//}
//
//@ExperimentalTime
//private fun fakeApiRx() {
//    val initialTime = System.currentTimeMillis()
//    disposable.add(
//        repository.getAllByRx()
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
////                .filter { user -> user.age % 2 == 0 }
////                .map { user ->
////                    user.age = user.age / 2
////                    return@map user
////                }
//            .subscribe(
//                { user -> printResult(user.age.toString()) },
//                { error -> Log.d(MainActivity.RxJava, error.toString()) },
//                {
//                    Log.d(MainActivity.RxJava, "Done")
//                    Log.d(MainActivity.TIME, (System.currentTimeMillis() - initialTime).toString())
//                }
//            )
//    )
//}
//
//@ObsoleteCoroutinesApi
//private fun initCoroutines() {
//    CoroutineScope(Dispatchers.Main).apply {
//        launch {
//            // context of the parent, main runBlocking coroutine
//            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(Dispatchers.Unconfined) {
//            // not confined -- will work with main thread
//            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(Dispatchers.Default) {
//            // will get dispatched to DefaultDispatcher
//            println("Default               : I'm working in thread ${Thread.currentThread().name}")
//        }
//        launch(newSingleThreadContext("MyOwnThread")) {
//            // will get its own new thread
//            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
//
//            withContext(Dispatchers.IO) {
//
//            }
//        }
//    }
//}
//
//private fun printResult(result: String) {
//    textView.text = result
//}
//
//private fun TextView.clear() {
//    text = ""
//    disposable.clear()
//}
//
//fun main() {
//    GlobalScope.launch {
//        val result = async {
//            coroutine(1, 1000)
//        }.await()
//
//        println(result)
//    }
//}
//
//fun childJob() {
//
//    val parenJob: Job = CoroutineScope(Dispatchers.IO).launch {
//
//        val childJob: Job = launch {
//
//        }
//    }
//}
//
//suspend fun coroutine(number: Int, delay: Long) {
//    println("Coroutine $number starts work")
//    delay(delay)
//    println("Coroutine $number has finished")
//}
//
//
//fun routine(number: Int, delay: Long) {
//    println("Routine $number starts work")
//    Thread.sleep(delay)
//    println("Routine $number has finished")
//}