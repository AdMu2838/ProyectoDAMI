package com.example.proyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.core.Chat

class ChatAdapter(val chatClick: (Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var chats: List<Chat> = emptyList()

    fun setData(list: List<Chat>) {
        chats = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentChat = chats[position]

        holder.bind(currentChat)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun getItem(position: Int): Chat {
        return chats[position]
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatNameText: TextView = itemView.findViewById(R.id.chatNameText)
        private val usersTextView: TextView = itemView.findViewById(R.id.usersTextView)

        fun bind(chat: Chat) {
            chatNameText.text = chat.name
            usersTextView.text = chat.users.joinToString(", ")

            itemView.setOnClickListener {
                chatClick(chat)
            }
        }
    }
}
