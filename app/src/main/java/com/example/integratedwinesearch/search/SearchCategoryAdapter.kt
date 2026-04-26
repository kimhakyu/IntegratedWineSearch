package com.example.integratedwinesearch.search

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.search.model.SearchCategoryItem

class SearchCategoryAdapter(
    private val items: List<SearchCategoryItem>
) : RecyclerView.Adapter<SearchCategoryAdapter.SearchCategoryViewHolder>() {

    inner class SearchCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCategoryBg: ImageView = view.findViewById(R.id.ivCategoryBg)
        val ivCategoryOverlay: ImageView = view.findViewById(R.id.ivCategoryOverlay)
        val tvCategoryTitle: TextView = view.findViewById(R.id.tvCategoryTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_category, parent, false)
        return SearchCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCategoryViewHolder, position: Int) {
        val item = items[position]

        holder.ivCategoryBg.setImageResource(item.imageResId)
        holder.tvCategoryTitle.text = item.title

        val overlay = holder.ivCategoryOverlay.drawable.mutate() as GradientDrawable
        overlay.setColor(Color.parseColor(item.overlayColor))
        holder.ivCategoryOverlay.setImageDrawable(overlay)
    }

    override fun getItemCount(): Int = items.size
}