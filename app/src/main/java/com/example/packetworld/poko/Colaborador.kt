package com.example.packetworld.poko

data class Colaborador(
    var idColaborador: Int,

    var noPersonal: String?,
    var nombre: String,
    var apellidoPaterno: String,
    var apellidoMaterno: String?,
    var curp: String,
    var correo: String,
    var telefono: String?,
    var foto: String?,
    var password: String?,
    var numeroLicencia: String?,

    var idRol: Int?,
    var idSucursal: Int?,
    var nombreRol: String?
)


