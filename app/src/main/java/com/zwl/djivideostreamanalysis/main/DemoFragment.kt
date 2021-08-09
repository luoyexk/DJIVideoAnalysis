package com.zwl.djivideostreamanalysis.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zwl.djivideostreamanalysis.DemoManifest.DEMO_LIST
import com.zwl.djivideostreamanalysis.databinding.FragmentDemoBinding

class DemoFragment : Fragment() {

    private lateinit var binding: FragmentDemoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentDemoBinding.inflate(inflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerview.apply {
            adapter = DemosAdapter(DEMO_LIST, onItemClickListener = { _, item ->
                item?.let {
                    start(item.activity, item.layout)
                }
            })
        }
    }

    private fun start(activity: Class<*>, layoutFileId: Int) {
        val intent = Intent(requireContext(), activity).apply {
            putExtra("layout_file_id", layoutFileId)
        }
        startActivity(intent)
    }
}