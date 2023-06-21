package com.example.proyecto.core
data class Product(
    val id: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val phoneNumber: String = "",
    val description: String = "",
    val category: String = "",
    var imageUrl: String = "",
    val userId: String = ""
)