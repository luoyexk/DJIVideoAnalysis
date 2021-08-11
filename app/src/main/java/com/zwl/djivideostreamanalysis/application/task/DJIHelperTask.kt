package com.zwl.djivideostreamanalysis.application.task

import android.app.Application
import com.secneo.sdk.Helper
import com.zwl.djivideostreamanalysis.application.InitializationTask

class DJIHelperTask : InitializationTask() {
    override fun start(application: Application) {
        Helper.install(application)
    }
}