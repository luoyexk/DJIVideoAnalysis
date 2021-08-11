package com.zwl.djivideostreamanalysis.ml

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.odml.image.BitmapMlImageBuilder
import com.zwl.djivideostreamanalysis.DemoManifest.DEMO_LIST_ML
import com.zwl.djivideostreamanalysis.MLUseCase
import com.zwl.djivideostreamanalysis.databinding.FragmentDemosDetailBinding
import com.zwl.djivideostreamanalysis.main.BaseDJICameraFragment
import com.zwl.djivideostreamanalysis.utils.drawBitmap
import com.zwl.djivideostreamanalysis.utils.toast
import dji.sdk.codec.DJICodecManager
import dji.thirdparty.afinal.core.AsyncTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MLFragment : BaseDJICameraFragment() {

    private lateinit var binding: FragmentDemosDetailBinding
    private val poseDetector = PoseDetectorProcessor()
    private var isScreenshot = false

    override fun getTextureView(): TextureView {
        return binding.playView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentDemosDetailBinding.inflate(inflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        poseDetector.stop()
    }

    private fun initRecyclerView() {
        binding.listView.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.select_dialog_singlechoice,
                android.R.id.text1,
                DEMO_LIST_ML
            )
            choiceMode = ListView.CHOICE_MODE_SINGLE
            setOnItemClickListener { _, _, position, _ ->
                when (MLUseCase.valueOf(DEMO_LIST_ML[position])) {
                    MLUseCase.DEFAULT -> enableDefault()
                    MLUseCase.POSE_DETECTION -> enablePoseDetect()
                }
                setItemChecked(position, true)
            }
            setItemChecked(0, true)
        }
    }

    private fun enableDefault() {
        if (mCodecManager != null) {
            mCodecManager!!.cleanSurface()
            mCodecManager!!.destroyCodec()
            mCodecManager = null
        }
        requireActivity().apply {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun enablePoseDetect() {
        mCodecManager?.cleanSurface()
        mCodecManager?.enabledYuvData(true)
        mCodecManager?.yuvDataCallback =
            DJICodecManager.YuvDataCallback { format, yuvFrame, dataSize, width, height ->
                updateTextureViewSize(width, height)
                val bytes = ByteArray(dataSize)
                yuvFrame.get(bytes)
                AsyncTask.execute {
                    when (format.getInteger(MediaFormat.KEY_COLOR_FORMAT)) {
                        MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar ->
                            if (Build.VERSION.SDK_INT <= 23) {
                                oldSaveYuvDataToJPEG(bytes, width, height)
                            } else {
                                newSaveYuvDataToJPEG(bytes, width, height)
                            }
                        MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar ->
                            newSaveYuvDataToJPEG420P(bytes, width, height)
                        else -> Unit
                    }
                }
            }
    }

    private var analysisInterval = 20L
    private val screenshotInterval = 2000L
    private var curTime = 0L

    private fun handle(bytes: ByteArray, width: Int, height: Int) {
        if (isScreenshot) {
            screenshot(bytes, width, height)
            return
        }
        analysis(bytes, width, height)
    }

    private fun analysis(bytes: ByteArray, width: Int, height: Int) {
        val yuvImage = YuvImage(bytes, ImageFormat.NV21, width, height, null)
        val op = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 20, op)
        val bitmap = BitmapFactory.decodeByteArray(op.toByteArray(), 0, op.size())
        val image = BitmapMlImageBuilder(bitmap).build()
        op.close()
        // todo image transform need improve
//        val image: MlImage = ByteBufferMlImageBuilder(
//            bytes,
//            width,
//            height,
//            MlImage.IMAGE_FORMAT_YUV_420_888
//        ).build()

//        val image = InputImage.fromByteArray(
//            bytes,
//            width,
//            height,
//            0,
//            InputImage.IMAGE_FORMAT_YUV_420_888
//        )
        binding.playView.drawBitmap(bitmap)
        if (analysisInterval < System.currentTimeMillis() - curTime) {
            curTime = System.currentTimeMillis()
            lifecycleScope.launch {
                poseDetector.detectInImage(image)
                    .catch { ex ->
                        ex.message?.let { toast(it) }
                    }
                    .collect {
                        val graphicOverlay = binding.graphicOverlay
                        graphicOverlay.clear()
                        graphicOverlay.add(
                            PoseGraphic(
                                graphicOverlay,
                                it,
                                showInFrameLikelihood = true,
                                visualizeZ = true,
                                rescaleZForVisualization = true,
                                poseClassification = emptyList()
                            )
                        )
                        graphicOverlay.postInvalidate()
                    }
            }
        }
    }

    private fun screenshot(bytes: ByteArray, width: Int, height: Int) {
        if (screenshotInterval < System.currentTimeMillis() - curTime) {
            curTime = System.currentTimeMillis()
            val yuvImage = YuvImage(bytes, ImageFormat.NV21, width, height, null)
            val file = File(
                Environment.getExternalStorageDirectory().toString() + "/DJI_ScreenShot",
                "/ScreenShot_" + System.currentTimeMillis() + ".jpg"
            )
            file.parentFile?.mkdirs()
            yuvImage.compressToJpeg(
                Rect(0, 0, width, height),
                100,
                FileOutputStream(file)
            )
            binding.playView.drawBitmap(BitmapFactory.decodeFile(file.absolutePath))
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "screenshot complete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // For android API <= 23
    private fun oldSaveYuvDataToJPEG(yuvFrame: ByteArray, width: Int, height: Int) {
        if (yuvFrame.size < width * height) {
            //DJILog.d(TAG, "yuvFrame size is too small " + yuvFrame.length);
            return
        }
        val y = ByteArray(width * height)
        val u = ByteArray(width * height / 4)
        val v = ByteArray(width * height / 4)
        val nu = ByteArray(width * height / 4)
        val nv = ByteArray(width * height / 4)
        System.arraycopy(yuvFrame, 0, y, 0, y.size)
        for (i in u.indices) {
            v[i] = yuvFrame[y.size + 2 * i]
            u[i] = yuvFrame[y.size + 2 * i + 1]
        }
        val uvWidth = width / 2
        val uvHeight = height / 2
        for (j in 0 until uvWidth / 2) {
            for (i in 0 until uvHeight / 2) {
                val uSample1 = u[i * uvWidth + j]
                val uSample2 = u[i * uvWidth + j + uvWidth / 2]
                val vSample1 = v[(i + uvHeight / 2) * uvWidth + j]
                val vSample2 = v[(i + uvHeight / 2) * uvWidth + j + uvWidth / 2]
                nu[2 * (i * uvWidth + j)] = uSample1
                nu[2 * (i * uvWidth + j) + 1] = uSample1
                nu[2 * (i * uvWidth + j) + uvWidth] = uSample2
                nu[2 * (i * uvWidth + j) + 1 + uvWidth] = uSample2
                nv[2 * (i * uvWidth + j)] = vSample1
                nv[2 * (i * uvWidth + j) + 1] = vSample1
                nv[2 * (i * uvWidth + j) + uvWidth] = vSample2
                nv[2 * (i * uvWidth + j) + 1 + uvWidth] = vSample2
            }
        }
        //nv21test
        val bytes = ByteArray(yuvFrame.size)
        System.arraycopy(y, 0, bytes, 0, y.size)
        for (i in u.indices) {
            bytes[y.size + i * 2] = nv[i]
            bytes[y.size + i * 2 + 1] = nu[i]
        }
        handle(bytes, width, height)
    }

    private fun newSaveYuvDataToJPEG(yuvFrame: ByteArray, width: Int, height: Int) {
        if (yuvFrame.size < width * height) {
            //DJILog.d(TAG, "yuvFrame size is too small " + yuvFrame.length);
            return
        }
        val length = width * height
        val u = ByteArray(width * height / 4)
        val v = ByteArray(width * height / 4)
        for (i in u.indices) {
            v[i] = yuvFrame[length + 2 * i]
            u[i] = yuvFrame[length + 2 * i + 1]
        }
        for (i in u.indices) {
            yuvFrame[length + 2 * i] = u[i]
            yuvFrame[length + 2 * i + 1] = v[i]
        }
        handle(yuvFrame, width, height)
    }

    private fun newSaveYuvDataToJPEG420P(yuvFrame: ByteArray, width: Int, height: Int) {
        if (yuvFrame.size < width * height) {
            return
        }
        val length = width * height
        val u = ByteArray(width * height / 4)
        val v = ByteArray(width * height / 4)
        for (i in u.indices) {
            u[i] = yuvFrame[length + i]
            v[i] = yuvFrame[length + u.size + i]
        }
        for (i in u.indices) {
            yuvFrame[length + 2 * i] = v[i]
            yuvFrame[length + 2 * i + 1] = u[i]
        }
        handle(yuvFrame, width, height)
    }
}