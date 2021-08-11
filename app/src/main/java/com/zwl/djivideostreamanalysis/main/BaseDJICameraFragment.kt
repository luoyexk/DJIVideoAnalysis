package com.zwl.djivideostreamanalysis.main

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import dji.sdk.camera.VideoFeeder
import dji.sdk.codec.DJICodecManager

abstract class BaseDJICameraFragment : Fragment() {

    protected var mCodecManager: DJICodecManager? = null
    private var mVideoFeederListener: VideoFeeder.VideoDataListener? = null

    abstract fun getTextureView(): TextureView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTextureView(getTextureView())
    }

    override fun onResume() {
        super.onResume()
        addVideoFeedListener()
    }

    override fun onPause() {
        super.onPause()
        removeVideoFeedListener()
    }


    override fun onDestroy() {
        super.onDestroy()
        clearCodec()
    }

   protected fun updateTextureViewSize(width: Int, height: Int) {
        requireActivity().runOnUiThread {
            val textureView = getTextureView()
            if (textureView.width != width) {
                textureView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    this.width = width
                    this.height = height
                }
            }
        }
    }

    private fun clearCodec() {
        mCodecManager?.cleanSurface()
        mCodecManager?.destroyCodec()
    }

    private fun initTextureView(textureView: TextureView) {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                if (mCodecManager == null) {
                    mCodecManager =
                        DJICodecManager(requireContext().applicationContext, surface, width, height)
                    //For M300RTK, you need to actively request an I frame.
                    mCodecManager?.resetKeyFrame()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mCodecManager?.cleanSurface()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }
    }

    private fun removeVideoFeedListener() {
        mVideoFeederListener?.let {
            VideoFeeder.getInstance()?.primaryVideoFeed?.removeVideoDataListener(it)
        }
    }

    private fun addVideoFeedListener() {
        if (mVideoFeederListener == null) {
            mVideoFeederListener = VideoFeeder.VideoDataListener { videoBuffer, size ->
                mCodecManager?.sendDataToDecoder(videoBuffer, size)
            }
        }
        mVideoFeederListener?.let {
            VideoFeeder.getInstance()?.primaryVideoFeed?.addVideoDataListener(it)
        }
    }
}