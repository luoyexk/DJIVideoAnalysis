package com.zwl.djivideostreamanalysis.ml

import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import timber.log.Timber

class PoseDetectorProcessor {

    private val client: PoseDetector

    init {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        client = PoseDetection.getClient(options)
    }

    fun detectInImage(inputImage: InputImage?) {
        val img = inputImage?.mediaImage
        if (img == null) {
            Timber.d("media image is null")
            return
        }
        val mlImage: MlImage = MediaMlImageBuilder(img)
            .setRotation(0)
            .build()
        client.process(mlImage)
            .addOnSuccessListener {
                Timber.d("suc ${it.allPoseLandmarks.size}")
            }
            .addOnFailureListener {
                Timber.e(it, "detect in image pose failed.")
            }
    }

    fun detectInImage(inputImage: MlImage): Flow<Pose> {
        return callbackFlow {
            client.process(inputImage)
                .addOnSuccessListener {
                    if (isActive) {
                        offer(it)
                    }
                    close()
                }
                .addOnFailureListener {
                    Timber.e(it, "detect in image pose failed.")
                    close(it)
                }
            awaitClose()
        }
    }


    fun stop() {
        client.close()
    }
}
