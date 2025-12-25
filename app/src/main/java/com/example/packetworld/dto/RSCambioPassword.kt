package com.example.packetworld.dto

data class RSCambioPassword(
    val idColaborador: Int,
    var passwordActual: String,
    var passwordNueva: String
)
