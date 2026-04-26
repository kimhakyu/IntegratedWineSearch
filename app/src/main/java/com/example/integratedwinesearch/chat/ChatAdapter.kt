package com.example.integratedwinesearch.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.chat.model.ChatMessage

class ChatAdapter(
    private val items: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_BOT = 0
        private const val VIEW_TYPE_USER = 1
    }

    inner class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBotMessage: TextView = view.findViewById(R.id.tvBotMessage)
        val tvBotTime: TextView = view.findViewById(R.id.tvBotTime)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserMessage: TextView = view.findViewById(R.id.tvUserMessage)
        val tvUserTime: TextView = view.findViewById(R.id.tvUserTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is BotViewHolder -> {
                holder.tvBotMessage.text = item.message
                holder.tvBotTime.text = item.time
            }
            is UserViewHolder -> {
                holder.tvUserMessage.text = item.message
                holder.tvUserTime.text = item.time
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addMessage(message: ChatMessage) {
        items.add(message)
        notifyItemInserted(items.lastIndex)
    }
}