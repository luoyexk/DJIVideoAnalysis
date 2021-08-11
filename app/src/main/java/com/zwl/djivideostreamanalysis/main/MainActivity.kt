package com.zwl.djivideostreamanalysis.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.zwl.djivideostreamanalysis.R
import com.zwl.djivideostreamanalysis.databinding.ActivityMainBinding
import com.zwl.djivideostreamanalysis.utils.DJISDKTool
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), DJISDKManager.SDKManagerCallback {

    private val animUtil by lazy { MainActivityAnimationUtil(findViewById(R.id.animationView)) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        requestPermissions()
        registerSDK()
    }

    private fun requestPermissions() {
        if (PERMISSIONS.any { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
            requestPermissions(PERMISSIONS, 12345)
        }
    }

    private fun registerSDK() {
        lifecycleScope.launch(Dispatchers.Default) {
            DJISDKManager.getInstance().registerApp(application, this@MainActivity)
        }
    }

    private fun showDemo() {
        if (supportFragmentManager.findFragmentByTag("demo") != null) {
            return
        }
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.core_push_up_in, R.anim.core_push_up_out)
            replace(R.id.fragment_container, DemoFragment(), "demo")
        }
    }

    private fun removeDemo() {
        supportFragmentManager.findFragmentByTag("demo")?.let {
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.core_push_up_in, R.anim.core_push_up_out)
                remove(it)
            }
        }
    }

    private fun updateTitle(displayName: String?) {
        title = displayName
    }

    private fun updateLog(text: String) {
        binding.tvLog.append("\n$text")
    }

    //region dji register callback
    override fun onRegister(p0: DJIError?) {
        runOnUiThread {
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
    }

    override fun onProductDisconnect() {
        runOnUiThread {
            updateTitle(getString(R.string.fpv_tip_disconnect))
            removeDemo()
            animUtil.showWaiting()
        }
    }

    override fun onProductConnect(p0: BaseProduct?) {
        runOnUiThread {
            animUtil.hideWaiting()
            updateTitle(DJISDKTool.aircraft?.model?.displayName)
            showDemo()
        }
    }

    override fun onProductChanged(p0: BaseProduct?) {
    }

    override fun onComponentChange(
        p0: BaseProduct.ComponentKey?,
        p1: BaseComponent?,
        p2: BaseComponent?
    ) {
        runOnUiThread {
            showDemo()
        }
    }

    override fun onInitProcess(p0: DJISDKInitEvent?, p1: Int) {
        runOnUiThread {
            updateTitle("Loading...")
        }
    }

    override fun onDatabaseDownloadProgress(current: Long, total: Long) {
    }
    //endregion
}

val PERMISSIONS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)