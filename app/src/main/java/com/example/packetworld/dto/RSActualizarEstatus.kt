package com.example.packetworld.dto

data class RSActualizarEstatus(
    val numeroGuia: String,
    val nuevoIdEstatus: Int,
    val comentario: String,
    val idColaborador: Int
)
