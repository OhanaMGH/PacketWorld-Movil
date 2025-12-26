package com.example.packetworld.dto

import com.example.packetworld.poko.Paquete
import java.util.List


data class RSEnvioDetalle(
    val idEnvio: Int,
    val numeroGuia: String,
    val estatus: String,

    val fechaEnvio: String?,
    val fechaEntrega: String?,
    val comentario: String?,
    val idEstatus: Int,
    val nombreDestinatario: String?,
    val apellidoPaternoDestinatario: String?,
    val apellidoMaternoDestinatario: String?,

    val clienteNombre: String?,
    val clienteApellidoPaterno: String?,
    val clienteApellidoMaterno: String?,
    val clienteTelefono: String?,
    val clienteCorreo: String?,

    val sucursalOrigen: String?,

    val dirCalle: String?,
    val dirNumero: String?,
    val dirColonia: String?,
    val dirCP: String?,
    val dirCiudad: String?,
    val dirEstado: String?,

    val paquetes: List<Paquete>?
)
