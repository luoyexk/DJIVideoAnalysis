package com.zwl.djivideostreamanalysis.application.task

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.zwl.djivideostreamanalysis.application.InitializationTask

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