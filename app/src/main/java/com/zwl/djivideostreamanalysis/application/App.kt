package com.zwl.djivideostreamanalysis.application

import android.app.Application
import android.content.Context
import com.zwl.djivideostreamanalysis.application.task.DJIHelperTask
import com.zwl.djivideostreamanalysis.application.task.LifecycleTask
import com.zwl.djivideostreamanalysis.application.task.TimberTask

class App : Application() {

    private val djiTask = DJIHelperTask()
    private val tasks = listOf(
        TimberTask(),
        LifecycleTask()
    )

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        djiTask.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        tasks.forEach { it.init(this) }
    }
}
