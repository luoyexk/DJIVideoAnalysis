package com.zwl.djivideostreamanalysis.application

import android.app.Application
import android.content.Context

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
