package com.example.packetworld.dto

import com.example.packetworld.poko.Envio


data class RSEnvioLista(
    val idEnvio: Int,
    val numeroGuia: String,
    val direccionDestino: String,
    val estatus: String

)