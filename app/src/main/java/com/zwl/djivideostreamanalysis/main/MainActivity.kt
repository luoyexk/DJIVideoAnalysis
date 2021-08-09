package com.zwl.djivideostreamanalysis.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.zwl.djivideostreamanalysis.R
import com.zwl.djivideostreamanalysis.utils.DJISDKTool
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), DJISDKManager.SDKManagerCallback {

    private val tvLog by lazy { findViewById<TextView>(R.id.tv_log) }
    private val animUtil by lazy { MainActivityAnimationUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerSDK()
    }

    private fun registerSDK() {
        thread(name = "dji-init-thread") {
            DJISDKManager.getInstance().registerApp(this.application, this)
        }
    }

    private fun showDemo() {
        runOnUiThread {
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.core_push_up_in, R.anim.core_push_up_out)
                replace(R.id.fragment_container, DemoFragment())
            }
        }
    }

    private fun removeDemo() {
        supportFragmentManager.popBackStack()
    }

    private fun updateTitle(displayName: String?) {
        runOnUiThread {
            title = displayName
        }
    }

    private fun updateLog(text: String) {
        runOnUiThread { tvLog.append("\n$text") }
    }


    override fun onRegister(p0: DJIError?) {
        when (p0) {
            DJISDKError.REGISTRATION_SUCCESS -> {
                DJISDKManager.getInstance().startConnectionToProduct()
                updateTitle("Wait for connection")
            }
            else -> {
                updateTitle("Register error")
                updateLog(p0?.toString().orEmpty())
            }
        }
        animUtil.showWaiting()
    }

    override fun onProductDisconnect() {
        updateTitle(getString(R.string.fpv_tip_disconnect))
        removeDemo()
        animUtil.showWaiting()
    }

    override fun onProductConnect(p0: BaseProduct?) {
        updateTitle(DJISDKTool.aircraftOrNull()?.model?.displayName)
        showDemo()
        animUtil.hideWaiting()
    }

    override fun onProductChanged(p0: BaseProduct?) {
    }

    override fun onComponentChange(
        p0: BaseProduct.ComponentKey?,
        p1: BaseComponent?,
        p2: BaseComponent?
    ) {
    }

    override fun onInitProcess(p0: DJISDKInitEvent?, p1: Int) {
        updateTitle("Loading...")
    }

    override fun onDatabaseDownloadProgress(current: Long, total: Long) {
    }
}