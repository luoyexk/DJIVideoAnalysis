package com.zwl.djivideostreamanalysis.application

import android.app.Application
import kotlin.system.measureNanoTime

abstract class InitializationTask {

    protected abstract fun start(application: Application)

    fun init(application: Application) {
        kotlin.runCatching {
            start(application)
        }
    }
}

