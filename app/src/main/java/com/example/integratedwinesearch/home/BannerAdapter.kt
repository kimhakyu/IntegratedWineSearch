package com.example.integratedwinesearch.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.home.model.BannerItem

class BannerAdapter(
    private val items: List<BannerItem>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBanner: ImageView = view.findViewById(R.id.ivBanner)
        val tvBannerTitle: TextView = view.findViewById(R.id.tvBannerTitle)
        val tvBannerSubTitle: TextView = view.findViewById(R.id.tvBannerSubTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = items[position]
        holder.ivBanner.setImageResource(item.imageResId)
        holder.tvBannerTitle.text = item.title
        holder.tvBannerSubTitle.text = item.subTitle
    }

    override fun getItemCount(): Int = items.size
}