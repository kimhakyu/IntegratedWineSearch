package com.example.integratedwinesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.CategoryItem

class CategoryAdapter(
    private val items: List<CategoryItem>,
    private val onCategoryClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCategory: ImageView = view.findViewById(R.id.ivCategory)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        if (!item.imageUrl.isNullOrBlank()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(item.imageResId)
                .error(item.imageResId)
                .into(holder.ivCategory)
        } else {
            holder.ivCategory.setImageResource(item.imageResId)
        }
        holder.tvCategoryName.text = item.name
        holder.itemView.setOnClickListener { onCategoryClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
