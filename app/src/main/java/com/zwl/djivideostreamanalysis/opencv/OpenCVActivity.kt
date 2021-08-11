package com.zwl.djivideostreamanalysis.opencv

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.commit
import com.zwl.djivideostreamanalysis.R
import com.zwl.djivideostreamanalysis.utils.toast
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import timber.log.Timber

/**
OpenCV – 4.5.3
https://opencv.org/releases/
https://github.com/opencv/opencv
 *
 */
class OpenCVActivity : AppCompatActivity() {

    private var mLoaderCallback: OpenCVLoaderCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_cv)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, OpenCVFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initOpenCV(application)
    }

    /**
     * initialize OpenCV sdk.
     */
    private fun initOpenCV(application: Application) {
        initCallback(application)
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, application, mLoaderCallback)
        } else {
            mLoaderCallback?.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private fun initCallback(application: Application) {
        if (mLoaderCallback == null) {
            mLoaderCallback = OpenCVLoaderCallback(application)
        }
    }
}

class OpenCVLoaderCallback(application: Application) : BaseLoaderCallback(application) {
    var isInitSuc = false
    override fun onManagerConnected(status: Int) {
        when (status) {
            LoaderCallbackInterface.SUCCESS -> {
                isInitSuc = true
                mAppContext.toast("OpenCV loaded successfully")
            }
            else -> {
                isInitSuc = false
                super.onManagerConnected(status)
                mAppContext.toast("Load error! Status[$status]")
            }
        }
    }
}