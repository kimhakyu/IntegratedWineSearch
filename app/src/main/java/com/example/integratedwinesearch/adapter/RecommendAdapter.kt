package com.example.integratedwinesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.WineItem
import java.text.DecimalFormat

class RecommendAdapter(
    private val items: List<WineItem>
) : RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder>() {

    private val decimalFormat = DecimalFormat("#,###")

    inner class RecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRecommendWine: ImageView = view.findViewById(R.id.ivRecommendWine)
        val tvRecommendType: TextView = view.findViewById(R.id.tvRecommendType)
        val tvRecommendName: TextView = view.findViewById(R.id.tvRecommendName)
        val tvRecommendDescription: TextView = view.findViewById(R.id.tvRecommendDescription)
        val tvRecommendGrade: TextView = view.findViewById(R.id.tvRecommendGrade)
        val tvRecommendPrice: TextView = view.findViewById(R.id.tvRecommendPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommend, parent, false)
        return RecommendViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        val item = items[position]

        holder.ivRecommendWine.setImageResource(item.imageResId)
        holder.tvRecommendType.text = item.type
        holder.tvRecommendName.text = item.name
        holder.tvRecommendDescription.text = item.description ?: ""
        holder.tvRecommendGrade.text = item.grade
        holder.tvRecommendPrice.text = "₩${decimalFormat.format(item.price)}"
    }

    override fun getItemCount(): Int = items.size
}