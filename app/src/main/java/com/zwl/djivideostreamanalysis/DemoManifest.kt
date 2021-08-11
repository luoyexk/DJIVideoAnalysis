package com.zwl.djivideostreamanalysis

import com.zwl.djivideostreamanalysis.main.DemosAdapter
import com.zwl.djivideostreamanalysis.ml.MLActivity
import com.zwl.djivideostreamanalysis.opencv.OpenCVActivity

object DemoManifest {

    val DEMO_LIST: Array<DemosAdapter.Demo> = arrayOf(
        DemosAdapter.Demo(
            title = "OpenCV",
            description = "Use OpenCV to process drone image",
            activity = OpenCVActivity::class.java
        ),
        DemosAdapter.Demo(
            title = "Google ML",
            description = "Use ML to analyze drone image",
            activity = MLActivity::class.java
        ),
    )
    val DEMO_LIST_OPEN_CV = OpenCVUseCase.values().map { it.name }
    val DEMO_LIST_ML = MLUseCase.values().map { it.name }

}

enum class OpenCVUseCase {
    DEFAULT,

    /**
     * Use OpenCV to convert the image to grayscale
     */
    GRAY,
}

enum class MLUseCase {
    DEFAULT,

    /**
     * Pose estimation
     * https://www.tensorflow.org/lite/examples/pose_estimation/overview
     */
    POSE_DETECTION,
}