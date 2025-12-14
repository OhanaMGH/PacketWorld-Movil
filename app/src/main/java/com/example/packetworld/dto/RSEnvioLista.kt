package com.example.packetworld.dto

data class RSEnvioLista(
    val idEnvio: Int,
    val numeroGuia: String,
    val direccionDestino: String,
    val estatus: String
)
