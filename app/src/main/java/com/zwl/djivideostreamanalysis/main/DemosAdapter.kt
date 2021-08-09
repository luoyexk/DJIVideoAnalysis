package com.zwl.djivideostreamanalysis.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.zwl.djivideostreamanalysis.R

class DemosAdapter(
    private val dataset: Array<Demo>,
    private val onItemClickListener: (View, Demo?) -> Unit
) :
    RecyclerView.Adapter<DemosAdapter.ViewHolder>() {

    data class Demo(
        val title: String,
        val description: String,
        val layout: Int = 0,
        val activity: Class<*>
    ) {
        constructor(title: String, description: String, activity: Class<*>) : this(
            title,
            description,
            0,
            activity
        )
    }

    class ViewHolder(layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {

        private var title = layout.findViewById(R.id.title) as TextView
        private var description = layout.findViewById(R.id.description) as TextView
        private var listener: ((View, Demo?) -> Unit)? = null
        private var demo: Demo? = null

        init {
            layout.setOnClickListener { view ->
                listener?.invoke(view, demo)
            }
        }

        fun bind(item: Demo, onItemClickListener: (View, Demo?) -> Unit) {
            title.text = item.title
            description.text = item.description
            demo = item
            listener = onItemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.row, parent, false) as ConstraintLayout
        return ViewHolder(row)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataset[position], onItemClickListener)
    }

    override fun getItemCount() = dataset.size
}