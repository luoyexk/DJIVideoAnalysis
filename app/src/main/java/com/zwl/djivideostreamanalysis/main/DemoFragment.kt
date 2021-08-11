package com.zwl.djivideostreamanalysis.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zwl.djivideostreamanalysis.DemoManifest.DEMO_LIST
import com.zwl.djivideostreamanalysis.databinding.FragmentDemoEntranceBinding

class DemoFragment : Fragment() {

    private lateinit var binding: FragmentDemoEntranceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentDemoEntranceBinding.inflate(inflater, container, false).let {
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
                    start(item.activity)
                }
            })
        }
    }

    private fun start(activity: Class<*>) {
        startActivity(Intent(requireContext(), activity))
    }
}