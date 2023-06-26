package com.example.proyecto.core
data class Product(
    val id: String = "",
    var title: String = "",
    var price: Double = 0.0,
    var phoneNumber: String = "",
    var description: String = "",
    var category: String = "",
    var imageUrl: String = "",
    val userId: String = ""
)