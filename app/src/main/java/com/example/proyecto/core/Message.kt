package com.example.proyecto.core

import java.util.Date

data class Message (
    var message: String = "",
    var from: String = "",
    var dob: Date = Date()
)