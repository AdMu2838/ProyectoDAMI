package com.example.proyecto.core

data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList()
)