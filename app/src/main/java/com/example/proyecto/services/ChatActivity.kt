package com.example.proyecto.services

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.example.proyecto.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {
    private lateinit var messageListView: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private lateinit var messageAdapter: ArrayAdapter<String>
    private lateinit var messageList: MutableList<String>

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageListView = findViewById(R.id.messageListView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        messageList = mutableListOf()
        messageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
        messageListView.adapter = messageAdapter

        // Obtén la referencia a la base de datos de Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().reference.child("messages")

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageEditText.text.clear()
            }
        }

        // Agrega un listener para recibir los nuevos mensajes de la base de datos
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(String::class.java)
                message?.let {
                    messageList.add(it)
                    messageAdapter.notifyDataSetChanged()
                    // Desplázate al último mensaje agregado
                    messageListView.smoothScrollToPosition(messageList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja los errores de la base de datos
            }

            // Implementa los demás métodos de ChildEventListener según sea necesario
        })
    }

    private fun sendMessage(messageText: String) {
        // Crea una nueva entrada en la base de datos con el mensaje enviado
        val messageRef = databaseReference.push()
        messageRef.setValue(messageText)
    }
}