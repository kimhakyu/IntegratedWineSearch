package com.example.integratedwinesearch.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.search.model.SearchWineItem
import java.text.DecimalFormat

class SearchWineAdapter(
    private val items: List<SearchWineItem>,
    private val onWineClick: (SearchWineItem) -> Unit
) : RecyclerView.Adapter<SearchWineAdapter.SearchWineViewHolder>() {

    private val decimalFormat = DecimalFormat("#,###")
    private var showNumberIndicators: Boolean = true

    inner class SearchWineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val ivWine: ImageView = view.findViewById(R.id.ivWine)
        val tvWineName: TextView = view.findViewById(R.id.tvWineName)
        val tvWineGrade: TextView = view.findViewById(R.id.tvWineGrade)
        val tvWineType: TextView = view.findViewById(R.id.tvWineType)
        val tvWineRegion: TextView = view.findViewById(R.id.tvWineRegion)
        val tvWinePrice: TextView = view.findViewById(R.id.tvWinePrice)
        val tvSearchCount: TextView = view.findViewById(R.id.tvSearchCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchWineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_wine, parent, false)
        return SearchWineViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchWineViewHolder, position: Int) {
        val item = items[position]

        holder.tvRank.text = item.rank.toString()
        holder.tvRank.visibility = if (showNumberIndicators) View.VISIBLE else View.GONE
        holder.tvSearchCount.visibility = if (showNumberIndicators) View.VISIBLE else View.GONE
        if (!item.imageUrl.isNullOrBlank()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.sample_wine_red)
                .error(R.drawable.sample_wine_red)
                .into(holder.ivWine)
        } else {
            holder.ivWine.setImageResource(item.imageResId)
        }
        holder.tvWineName.text = item.name
        holder.tvWineGrade.text = item.grade
        holder.tvWineType.text = item.type
        holder.tvWineRegion.text = item.description?.takeIf { it.isNotBlank() } ?: item.region
        holder.tvWinePrice.text = "₩${decimalFormat.format(item.price)}"
        holder.tvSearchCount.text = "${decimalFormat.format(item.searchCount)} 검색"
        holder.itemView.setOnClickListener { onWineClick(item) }
    }

    fun setShowNumberIndicators(show: Boolean) {
        if (showNumberIndicators == show) return
        showNumberIndicators = show
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
