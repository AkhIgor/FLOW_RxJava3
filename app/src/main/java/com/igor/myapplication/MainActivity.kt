package com.igor.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var countButtonRx: Button
    private lateinit var countButtonFlow: Button
    private lateinit var clearButton: Button
    private val repository = Repository()
    private val disposable = CompositeDisposable()

    @ObsoleteCoroutinesApi
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text_view)
        countButtonRx = findViewById(R.id.rx_button)
        countButtonFlow = findViewById(R.id.flow_button)
        clearButton = findViewById(R.id.clear_button)

        countButtonRx.setOnClickListener {
            fakeApiRx()
        }

        countButtonFlow.setOnClickListener {
            fakeApiFlow()
        }

        clearButton.setOnClickListener {
            textView.clear()
        }

        main()
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    private fun fakeApiFlow() {
        CoroutineScope(Default).launch {
            val users: Flow<User> = repository.getAllByFlow()
                .filter { user -> user.age % 2 == 0 }
                .map { user ->
                    user.age = user.age / 2
                    return@map user
                }
            val time = measureTime {
                users.collect { user ->
                    launch(Main) {
                        Log.d(FLOW, user.age.toString())
                        Log.d(THREAD, Thread.currentThread().name)
                        printResult(user.age.toString())
                    }
                }
            }
            Log.d(TIME, time.toString())
        }
//        CoroutineScope(Main).launch {
//            repository.getAllByFlow()
//                .flowOn(IO)
//                .map { user ->
//                    Log.d(THREAD, Thread.currentThread().name)
//                    user.age = user.age / 2
//                    return@map user
//                }
//                .flowOn(Default)
//                .collect { user ->
//                    Log.d(FLOW, user.age.toString())
//                    Log.d(THREAD, Thread.currentThread().name)
//                    printResult(user.age.toString())
//                }
//        }
    }

    @ExperimentalTime
    private fun fakeApiRx() {
        val initialTime = System.currentTimeMillis()
        disposable.add(
            repository.getAllByRx()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
//                .filter { user -> user.age % 2 == 0 }
//                .map { user ->
//                    user.age = user.age / 2
//                    return@map user
//                }
                .subscribe(
                    { user -> printResult(user.age.toString()) },
                    { error -> Log.d(RxJava, error.toString()) },
                    {
                        Log.d(RxJava, "Done")
                        Log.d(TIME, (System.currentTimeMillis() - initialTime).toString())
                    }
                )
        )
    }

    @ObsoleteCoroutinesApi
    private fun initCoroutines() {
        CoroutineScope(Main).apply {
            launch {
                // context of the parent, main runBlocking coroutine
                println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Unconfined) {
                // not confined -- will work with main thread
                println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Default) {
                // will get dispatched to DefaultDispatcher
                println("Default               : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(newSingleThreadContext("MyOwnThread")) {
                // will get its own new thread
                println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            }
        }
    }

    private fun printResult(result: String) {
        textView.text = result
    }

    private fun TextView.clear() {
        text = ""
        disposable.clear()
    }

    fun main() {
        GlobalScope.launch {
            println("main starts")
            joinAll(
                async { coroutine(1, 500) },
                async { coroutine(2, 300) }
            )
            println("main ends")
        }
    }

    suspend fun coroutine(number: Int, delay: Long) {
        println("Coroutine $number starts work")
        delay(delay)
        println("Coroutine $number has finished")
    }


    fun routine(number: Int, delay: Long) {
        println("Routine $number starts work")
        Thread.sleep(delay)
        println("Routine $number has finished")
    }

    companion object {
        const val THREAD = "Thread"
        const val FLOW = "Flow"
        const val RxJava = "RxJava"

        const val TIME = "Time"

        private const val THREAD_WORKER_FILTER = "Thread: DefaultDispatcher-worker-"
    }
}