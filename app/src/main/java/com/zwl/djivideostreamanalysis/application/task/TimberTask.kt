package com.zwl.djivideostreamanalysis.application.task

import android.app.Application
import android.util.Log
import com.zwl.djivideostreamanalysis.BuildConfig
import com.zwl.djivideostreamanalysis.application.InitializationTask
import timber.log.Timber

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