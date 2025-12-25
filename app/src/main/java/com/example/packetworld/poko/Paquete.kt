package com.example.packetworld.poko

data class Paquete(
    val idPaquete: Int,
    val descripcion: String,
    val peso: String,
    val alto: String,
    val ancho: String,
    val profundidad: String,
    val idEnvio: Int? = null
)