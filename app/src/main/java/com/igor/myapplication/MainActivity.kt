package com.igor.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
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

    @ExperimentalCoroutinesApi
    suspend fun CoroutineScope.produceNumbers() = produce {
        var x = 1
        while (true) {
            send(x++) // produce next
            delay(100) // wait 0.1s
        }
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce {
        for (x in numbers) send(x * x)
    }

    @ExperimentalCoroutinesApi
    private fun main() {
        val parentJob = CoroutineScope(IO).launch {

            val childJob_1 = launch {
                val result = getResult(1)   //delay for 1s
                printResult(result)
            }


            try {
                val childJob_2 = launch {
                    val result = getResult(2)   //delay for 2s
                    throw IllegalArgumentException()
                    printResult(result)
                    cancel()
                }
            } catch (e: CancellationException) {
                println("IllegalArgumentException")
            }

            val childJob_3 = launch {
                val result = getResult(3)   //delay for 3s
                printResult(result)
            }
        }
    }

    suspend fun s() {
        withContext(IO) {

        }
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (data in channel) {
            if (data == 3) {
                cancel()
            } else println("Processor #$id received $data")
        }
    }

//    val channel = Channel<String>()
//
//        val producerJob = CoroutineScope(IO).launch {
//            for (emitNumber in 1..5) {
//                delay(1000)
//                println("Sending result: $emitNumber")
//                channel.send(emitNumber.toString())
//            }
//            channel.close()
//        }
//
//        val consumerJob = CoroutineScope(IO).launch {
//            for (result in channel)
//                println("Got new result: $result")
//        }

    private suspend fun getResult(time: Long): String {
        delay(time * 1000)
        return time.toString()
    }

    @ExperimentalCoroutinesApi
    private fun produceData(scope: CoroutineScope): ReceiveChannel<String> {
        return scope.produce(capacity = 5) {
            for (emitNumber in 1..5) {
                delay(1000)
                println("Sending result: $emitNumber")
                channel.send(emitNumber.toString())
            }
        }
    }

    private fun printResult(number: String) {
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