package com.igor.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var launchButton: Button
    private lateinit var asyncButton: Button
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
        launchButton = findViewById(R.id.coroutine_launch_button)
        asyncButton = findViewById(R.id.coroutine_async_button)
        countButtonRx = findViewById(R.id.rx_button)
        countButtonFlow = findViewById(R.id.flow_button)
        clearButton = findViewById(R.id.clear_button)

        clearButton.setOnClickListener { textView.setText(R.string.app_name) }

        launchButton.setOnClickListener {
            launchWork()
        }

        asyncButton.setOnClickListener {
            asyncWork()
        }
    }

    override fun onStart() {
        super.onStart()

        main()
    }

    private fun launchSomeWorkInBackground() {
        runBlocking {
            printText("Waiting for results")
            delay(3_000) // call suspend function
            println("Thread: ${Thread.currentThread()}")
            printText("Success!")
        }
    }

    private fun asyncSomeWorkInBackground() {
        CoroutineScope(IO).launch {
            val result1 = async {
                getResults()
            }.await()

            val result2 = async {
                getResults()
            }.await()

            val result3 = async {
                getResults()
            }.await()

            printText(result1 + result2 + result3)
        }
    }

    private fun asyncWork() {
        GlobalScope.launch {
            val result: Deferred<String> = async {
                "cool"
            }
            printText(result.await())
        }
    }

    private fun launchWork() {
        GlobalScope.launch {
            var result = ""
            launch {
                result = "cool"
            }.join()
            printText(result)
        }
    }

    private suspend fun printText(text: String) {
        withContext(Main) {
            textView.text = text
        }
    }

    private suspend fun getResults(): String {
        printText("Waiting for results")
        delay(3_000) // call suspend function
        return "Success!"
    }

    private object CoroutineHandler: CoroutineExceptionHandler{
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            TODO("Not yet implemented")
        }

    }


    private fun main() {
        val parentJob = CoroutineScope(IO).launch {
            val childJob_1 = launch {
                val result = getResult(1)   //delay for 1s
                printResult(result)
            }

            val childJob_2 = launch {
                val result = getResult(2)   //delay for 2s
//                throw IllegalArgumentException("IllegalArgumentException")
                printResult(result)
            }

            val childJob_3 = launch {
                val result = getResult(3)   //delay for 3s
                printResult(result)
            }

            childJob_2.cancel()
        }

        parentJob.invokeOnCompletion { e ->
            when (e) {
                null -> {
                    println("Parent job finished successfully")
                }
                is CancellationException -> {
                    println("Parent job was canceled")
                }
                else -> {
                    println("Parent job failed with exception ${e.message}")
                }
            }
        }
    }

    private suspend fun getResult(time: Long): String {
        delay(time * 1000)
        return time.toString()
    }

    private suspend fun printResult(number: String) {
        println("childJob #$number has finished with result $number")
    }

    companion object {
        const val THREAD = "Thread"
        const val FLOW = "Flow"
        const val RxJava = "RxJava"

        const val TIME = "Time"

        private const val THREAD_WORKER_FILTER = "Thread: DefaultDispatcher-worker-"
    }
}