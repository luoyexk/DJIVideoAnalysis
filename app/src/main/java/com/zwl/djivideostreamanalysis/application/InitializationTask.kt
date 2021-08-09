package com.zwl.djivideostreamanalysis.application

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.secneo.sdk.Helper
import com.zwl.djivideostreamanalysis.BuildConfig
import timber.log.Timber
import kotlin.system.measureNanoTime

abstract class InitializationTask {

    val name: String
        get() = this::class.java.simpleName

    protected abstract fun start(application: Application)

    fun init(application: Application) {
        var initResult: Result<Unit>? = null
        taskStart(name)
        val cost = measureNanoTime {
            initResult = kotlin.runCatching {
                start(application)
            }
        }
        taskInitFinished(name, cost, initResult)
    }

    private fun taskStart(name: String) {
        val message = "$name init start"
        print(message)
    }

    private fun taskInitFinished(name: String, cost: Long, result: Result<Unit>?) {
        val errMsg = result?.exceptionOrNull()?.stackTraceToString()
        val message = "$name init finished, cost: $cost ns err: $errMsg"
        print(message)
    }
}

class DJIHelperTask : InitializationTask() {
    override fun start(application: Application) {
        Helper.install(application)
    }
}

class TimberTask : InitializationTask() {

    override fun start(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                        return
                    }
                    // record it
                }
            })
        }
    }
}

class LifecycleTask : InitializationTask() {

    override fun start(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onMoveToBackground() {
                val message = "App running in the background, please fly safely"
                Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}
