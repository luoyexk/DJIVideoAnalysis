package com.zwl.djivideostreamanalysis.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.zwl.djivideostreamanalysis.R
import com.zwl.djivideostreamanalysis.utils.LottieTool
import dji.ux.beta.core.extension.hide
import dji.ux.beta.core.extension.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityAnimationUtil(private val activity: AppCompatActivity) {

    private val animationZip = "animation/loading_drone.zip"
    private lateinit var animationView: LottieAnimationView

    fun showWaiting() {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            initAnimationView()
            animationView.show()
            if (!animationView.isAnimating) {
                animationView.playAnimation()
            }
        }
    }

    private fun initAnimationView() {
        if (!this@MainActivityAnimationUtil::animationView.isInitialized) {
            animationView = activity.findViewById(R.id.animationView)
            LottieTool.load(activity, animationZip, animationView)
        }
    }

    fun hideWaiting() {
        activity.lifecycleScope.launch {
            animationView.cancelAnimation()
            animationView.hide()
        }
    }
}
