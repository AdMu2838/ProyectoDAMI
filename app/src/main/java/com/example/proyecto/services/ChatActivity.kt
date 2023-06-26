package com.example.proyecto.services

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.adapters.MessageAdapter
import com.example.proyecto.core.Message
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private var chatId = ""
    private var user = ""

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.getStringExtra("chatId") ?: ""
        user = intent.getStringExtra("user") ?: ""

        if (chatId.isNotEmpty() && user.isNotEmpty()) {
            initAdapter()
            initViews()
        }
    }
    private fun initAdapter() {
        val messageAdapter = MessageAdapter(user)
        val messagesRecyclerView: RecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.adapter = messageAdapter
    }
    private fun initViews() {
        val messagesRecyclerView: RecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        val messageAdapter = MessageAdapter(user)
        messagesRecyclerView.adapter = messageAdapter

        val messageTextField: EditText = findViewById(R.id.messageTextField)
        val sendMessageButton: Button = findViewById(R.id.sendMessageButton)
        sendMessageButton.setOnClickListener { sendMessage() }

        val chatRef = db.collection("chats").document(chatId)

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Message::class.java)
                messageAdapter.setData(listMessages)
            }

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if (error == null) {
                    messages?.let {
                        val listMessages = it.toObjects(Message::class.java)
                        messageAdapter.setData(listMessages)
                    }
                }
            }
    }

    private fun sendMessage() {
        val messageTextField: EditText = findViewById(R.id.messageTextField)
        val message = Message(
            message = messageTextField.text.toString(),
            from = user
        )

        db.collection("chats").document(chatId).collection("messages").document().set(message)

        messageTextField.setText("")
    }
}