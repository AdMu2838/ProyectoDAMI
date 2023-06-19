package com.example.proyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.adapters.ChatAdapter
import com.example.proyecto.core.Chat
import com.example.proyecto.core.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: MutableList<Chat>
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)

        chatList = mutableListOf()
        chatAdapter = ChatAdapter(chatList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
        searchButton.setOnClickListener {
            val email = searchEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                searchUserByEmail(email)
            }
        }
        databaseReference = FirebaseDatabase.getInstance().reference.child("chats")

        return view
    }
    private fun searchUserByEmail(email: String) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val user = userDocument.toObject(User::class.java)
                    if (user != null) {
                        val chatId = generateChatId(user.email) // Generar un identificador de chat único
                        navigateToChat(chatId, user)
                    }
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores de Firestore
            }
    }
    private fun generateChatId(email: String): String {
        // Implementa tu lógica para generar un identificador único para el chat
        // Por ejemplo, puedes concatenar los correos electrónicos de los usuarios y aplicar un hash
        // En este ejemplo, simplemente se concatenan los correos electrónicos sin hash
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentEmail = currentUser?.email
        return if (currentEmail != null) {
            "$currentEmail-$email"
        } else {
            ""
        }
    }

    private fun navigateToChat(chatId: String, user: User) {

    }

}