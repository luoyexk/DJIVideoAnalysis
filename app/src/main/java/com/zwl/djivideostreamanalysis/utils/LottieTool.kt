package com.zwl.djivideostreamanalysis.utils

import android.content.Context
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.zip.ZipInputStream

object LottieTool {
    fun load(context: Context, fileName: String, imageView: LottieAnimationView) {
        LottieCompositionFactory.fromZipStream(ZipInputStream(context.assets.open(fileName)), fileName)
            .addListener(SuccessListener(imageView))
            .addFailureListener(FailureListener(fileName))
    }
}

private class SuccessListener(imageView: LottieAnimationView) : LottieListener<LottieComposition> {
    private val ref = WeakReference(imageView)
    override fun onResult(result: LottieComposition?) {
        result?.let {
            ref.get()?.setComposition(it)
            ref.get()?.frame
        }
    }
}

private class FailureListener(private val fileName: String) : LottieListener<Throwable> {
    override fun onResult(result: Throwable?) {
        result?.let {
            Timber.e(it, "Lottie load assets failed. File name[$fileName]")
        }
    }
}