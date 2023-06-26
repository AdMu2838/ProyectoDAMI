package com.example.proyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.core.Message

class MessageAdapter(private val user: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var messages: List<Message> = emptyList()

    fun setData(list: List<Message>) {
        messages = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_message,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        if (user == message.from) {
            holder.myMessageLayout.visibility = View.VISIBLE
            holder.otherMessageLayout.visibility = View.GONE

            holder.myMessageTextView.text = message.message
            holder.othersMessageTextView.text = "" // Para asegurarse de que el otro mensaje esté vacío
        } else {
            holder.myMessageLayout.visibility = View.GONE
            holder.otherMessageLayout.visibility = View.VISIBLE

            holder.myMessageTextView.text = "" // Para asegurarse de que el mensaje propio esté vacío
            holder.othersMessageTextView.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myMessageLayout: LinearLayout = itemView.findViewById(R.id.myMessageLayout)
        val otherMessageLayout: LinearLayout = itemView.findViewById(R.id.otherMessageLayout)
        val myMessageTextView: TextView = itemView.findViewById(R.id.myMessageTextView)
        val othersMessageTextView: TextView = itemView.findViewById(R.id.othersMessageTextView)
    }
}