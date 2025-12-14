package com.example.packetworld.poko

data class Colaborador(
    val idColaborador: Int,
    val noPersonal: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val curp: String,
    val correo: String,
    val telefono: String?,
    val fotoBase64: String?,
    val numeroLicencia: String?,
    val idRol: Int,
    val idSucursal: Int,
    val nombreRol: String
)

