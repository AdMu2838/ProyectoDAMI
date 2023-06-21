package com.example.proyecto.core
data class Product(
    val title: String,
    val price: Double,
    val phoneNumber: String,
    val description: String,
    val category: String,
    var imageUrl: String // URL de la imagen en Firebase Storage
) {
    // Constructor vacío necesario para la deserialización de Firestore
    constructor() : this("", 0.0, "", "", "", "")
}