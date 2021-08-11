package com.zwl.djivideostreamanalysis.main

import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.zwl.djivideostreamanalysis.utils.LottieTool

class MainActivityAnimationUtil(private val animationView: LottieAnimationView) {

    private val animationZip = "animation/loading_drone.zip"

    fun showWaiting() {
        animationView.visibility = View.VISIBLE
        if (!animationView.isAnimating) {
            animationView.playAnimation()
        }
    }

    fun hideWaiting() {
        animationView.cancelAnimation()
        animationView.visibility = View.GONE
    }

    init {
        LottieTool.load(animationView.context, animationZip, animationView)
    }
}
