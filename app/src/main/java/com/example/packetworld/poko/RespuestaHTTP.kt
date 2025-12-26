package com.example.packetworld.poko

data class RespuestaHTTP(
    var codigo: Int = 0, // Código de respuesta HTTP (200, 404, 500)
    var contenido: String? = null // Contenido JSON de la respuesta
)
