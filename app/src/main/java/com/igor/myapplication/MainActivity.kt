package com.igor.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var countButtonRx: Button
    private lateinit var countButtonFlow: Button
    private lateinit var clearButton: Button
    private val repository = Repository()
    private val disposable = CompositeDisposable()

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
    }

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
    }

    @ExperimentalTime
    private fun fakeApiRx() {
        val initialTime = System.currentTimeMillis()
        disposable.add(
            repository.getAllByRx()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { user -> user.age % 2 == 0 }
                .map { user ->
                    user.age = user.age / 2
                    return@map user
                }
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

    private fun printResult(result: String) {
        textView.text = result
    }

    private fun TextView.clear() {
        text = ""
        disposable.clear()
    }

    companion object {
        const val THREAD = "Thread"
        const val FLOW = "Flow"
        const val RxJava = "RxJava"

        const val TIME = "Time"

        private const val THREAD_WORKER_FILTER = "Thread: DefaultDispatcher-worker-"
    }
}