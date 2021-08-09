package com.zwl.djivideostreamanalysis

import com.zwl.djivideostreamanalysis.main.DemosAdapter
import com.zwl.djivideostreamanalysis.ml.MLActivity
import com.zwl.djivideostreamanalysis.opencv.OpenCVActivity

object DemoManifest {

    val DEMO_LIST: Array<DemosAdapter.Demo> = arrayOf(
        DemosAdapter.Demo("OpenCV", "", OpenCVActivity::class.java),
        DemosAdapter.Demo("Google ML", "", MLActivity::class.java),
    )

}