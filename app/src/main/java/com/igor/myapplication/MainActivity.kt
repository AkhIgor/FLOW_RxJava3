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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var countButton: Button
    private lateinit var clearButton: Button
    private val repository = Repository()
    private val disposable = CompositeDisposable()

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text_view)
        countButton = findViewById(R.id.count_button)
        clearButton = findViewById(R.id.clear_button)

        countButton.setOnClickListener {
            fakeApiRx()
        }

        clearButton.setOnClickListener {
            textView.clear()
        }
    }

    @ExperimentalTime
    private fun fakeApiFlow() {
        CoroutineScope(Default).launch {
            val users: Flow<User> = repository.getAllByFlow()
            users.collect { user ->
                async(Main) {
                    Log.d("flow", user.age.toString())
                }
                // withContext(Main) {
                // //     printResult(user.age.toString())
                //     Log.d("flow", user.age.toString())
                // }
                // Log.d("flow", user.age.toString())
            }
        }
    }

    private fun fakeApiRx() {
        disposable.add(
            repository.getAllByRx()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { user ->  printResult(user.age.toString()) },
                    { error -> Log.d("RXJava 3", error.toString()) },
                    { Log.d("rx", "Done") }
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
}