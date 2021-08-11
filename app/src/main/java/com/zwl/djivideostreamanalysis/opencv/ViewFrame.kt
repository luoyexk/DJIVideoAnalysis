package com.zwl.djivideostreamanalysis.opencv

import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.nio.ByteBuffer

class ViewFrame(
    private val yuvFrame: ByteBuffer,
    private val width: Int,
    private val height: Int
) : CameraBridgeViewBase.CvCameraViewFrame {

    private var mRgba = Mat()
    private var mGray = Mat()

    override fun rgba(): Mat {
        // wip
//        val src = Mat(height, width, CvType.CV_8UC3, yuvFrame)
//        Imgproc.cvtColor(src, mRgba, Imgproc.COLOR_YUV2RGBA_I420)

        return mRgba
    }

    override fun gray(): Mat {
        this.mGray = Mat(height, width, CvType.CV_8UC1, yuvFrame)
        return this.mGray
    }

    fun release() {
        mGray.release()
        mRgba.release()
    }
}