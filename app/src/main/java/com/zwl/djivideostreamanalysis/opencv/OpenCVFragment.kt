package com.zwl.djivideostreamanalysis.opencv

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import com.zwl.djivideostreamanalysis.DemoManifest.DEMO_LIST_OPEN_CV
import com.zwl.djivideostreamanalysis.OpenCVUseCase
import com.zwl.djivideostreamanalysis.databinding.FragmentDemosDetailBinding
import com.zwl.djivideostreamanalysis.main.BaseDJICameraFragment
import com.zwl.djivideostreamanalysis.utils.drawBitmap
import dji.sdk.codec.DJICodecManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.Utils

class OpenCVFragment : BaseDJICameraFragment() {

    private lateinit var binding: FragmentDemosDetailBinding

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

    private fun initRecyclerView() {
        binding.listView.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.select_dialog_singlechoice,
                android.R.id.text1,
                DEMO_LIST_OPEN_CV
            )
            choiceMode = ListView.CHOICE_MODE_SINGLE
            setOnItemClickListener { _, _, position, _ ->
                when (OpenCVUseCase.valueOf(DEMO_LIST_OPEN_CV[position])) {
                    OpenCVUseCase.DEFAULT -> enableDefaultUseCase()
                    OpenCVUseCase.GRAY -> enableGray()
                }
                setItemChecked(position, true)
            }
            setItemChecked(0, true)
        }
    }

    private fun enableDefaultUseCase() {
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

    private fun enableGray() {
        mCodecManager?.cleanSurface()
        mCodecManager?.enabledYuvData(true)
        mCodecManager?.yuvDataCallback =
            DJICodecManager.YuvDataCallback { _, yuvFrame, _, width, height ->
                updateTextureViewSize(width, height)
                lifecycleScope.launch(Dispatchers.IO) {
                    val frameMat = ViewFrame(yuvFrame, width, height)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    Utils.matToBitmap(frameMat.gray(), bitmap)

                    binding.playView.drawBitmap(bitmap)
                }
            }
    }

}