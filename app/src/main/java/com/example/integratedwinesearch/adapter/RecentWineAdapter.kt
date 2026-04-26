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

class RecentWineAdapter(
    private val items: List<WineItem>
) : RecyclerView.Adapter<RecentWineAdapter.RecentWineViewHolder>() {

    private val decimalFormat = DecimalFormat("#,###")

    inner class RecentWineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRecentWine: ImageView = view.findViewById(R.id.ivRecentWine)
        val tvRecentType: TextView = view.findViewById(R.id.tvRecentType)
        val tvRecentName: TextView = view.findViewById(R.id.tvRecentName)
        val tvRecentViewedTime: TextView = view.findViewById(R.id.tvRecentViewedTime)
        val tvRecentGrade: TextView = view.findViewById(R.id.tvRecentGrade)
        val tvRecentPrice: TextView = view.findViewById(R.id.tvRecentPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentWineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_wine, parent, false)
        return RecentWineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentWineViewHolder, position: Int) {
        val item = items[position]

        holder.ivRecentWine.setImageResource(item.imageResId)
        holder.tvRecentType.text = item.type
        holder.tvRecentName.text = item.name
        holder.tvRecentViewedTime.text = item.viewedTime ?: ""
        holder.tvRecentGrade.text = item.grade
        holder.tvRecentPrice.text = "₩${decimalFormat.format(item.price)}"
    }

    override fun getItemCount(): Int = items.size
}