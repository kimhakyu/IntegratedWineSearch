package com.example.integratedwinesearch.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.WineItem
import java.text.DecimalFormat

class BestSellerAdapter(
    private val items: List<WineItem>,
    private val onWineClick: (WineItem) -> Unit
) : RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder>() {

    private val decimalFormat = DecimalFormat("#,###")
    private val tag = "BestSellerAdapter"

    inner class BestSellerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivWineImage: ImageView = view.findViewById(R.id.ivWineImage)
        val tvWineGrade: TextView = view.findViewById(R.id.tvWineGrade)
        val tvWineType: TextView = view.findViewById(R.id.tvWineType)
        val tvWineName: TextView = view.findViewById(R.id.tvWineName)
        val tvWinePrice: TextView = view.findViewById(R.id.tvWinePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_best_seller, parent, false)
        return BestSellerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        val item = items[position]

        if (!item.imageUrl.isNullOrBlank()) {
            Log.d(tag, "이미지 로드 시도 url=${item.imageUrl}")
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.sample_wine_red)
                .error(R.drawable.sample_wine_red)
                .into(holder.ivWineImage)
        } else {
            holder.ivWineImage.setImageResource(item.imageResId)
        }

        holder.tvWineGrade.text = item.grade
        holder.tvWineType.text = item.type
        holder.tvWineName.text = item.name
        holder.tvWinePrice.text = "₩${decimalFormat.format(item.price)}"
        holder.itemView.setOnClickListener { onWineClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
