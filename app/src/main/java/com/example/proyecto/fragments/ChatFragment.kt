package com.example.proyecto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.adapters.ChatAdapter
import com.example.proyecto.core.Chat
import com.example.proyecto.services.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID
class ChatFragment : Fragment() {
    private var user = ""
    private val db = Firebase.firestore
    private lateinit var newChatText: EditText
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserEmail = currentUser?.email
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        arguments?.getString("user")?.let { user = it }

        if (user.isNotEmpty()) {
            initViews(view)
        }

        return view
    }

    private fun initViews(view: View) {
        val newChatButton: Button = view.findViewById(R.id.newChatButton)
        val listChatsRecyclerView: RecyclerView = view.findViewById(R.id.listChatsRecyclerView)
        newChatText = view.findViewById(R.id.newChatText)

        newChatButton.setOnClickListener { newChat() }

        listChatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listChatsRecyclerView.adapter = ChatAdapter { chat ->
            chatSelected(chat)
        }

        val userRef = db.collection("users").document(user)

        userRef.collection("chats")
            .get()
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)
                (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            .addSnapshotListener { chats, error ->
                if (error == null) {
                    chats?.let {
                        val listChats = it.toObjects(Chat::class.java)
                        (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                    }
                }
            }
    }

    private fun chatSelected(chat: Chat) {
        val chatId = chat.id
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun newChat() {
        val chatId = UUID.randomUUID().toString()
        val otherUserEmail = newChatText.text.toString()
        val currentUserEmail = currentUser?.email

        if (currentUserEmail != null && otherUserEmail.isNotEmpty()) {
            val users = listOf(currentUserEmail, otherUserEmail)

            val chat = Chat(
                id = chatId,
                name = "Chat con $otherUserEmail",
                users = users as List<String>
            )

            try {
                db.collection("chats").document(chatId).set(chat)
                db.collection("users").document(currentUserEmail).collection("chats").document(chatId)
                    .set(chat)
                db.collection("users").document(otherUserEmail).collection("chats").document(chatId)
                    .set(chat)

                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("chatId", chatId)
                intent.putExtra("userEmail", currentUserEmail)
                startActivity(intent)
            } catch (e: Exception) {
                // Mostrar una alerta de que el usuario no ha sido encontrado
                Toast.makeText(requireContext(), "No se encontró al usuario", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Mostrar una alerta indicando que falta información
            Toast.makeText(requireContext(), "Falta información del usuario", Toast.LENGTH_SHORT).show()
        }
    }
}