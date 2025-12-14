package com.example.packetworld.poko
import java.math.BigDecimal
data class Envio(
    val idEnvio: Int,
    val numeroGuia: String,
    val fechaEnvio: String,
    val fechaEntrega: String,
    var nombreDestinatario: String,
    val apellidoPaternoDestinatario: String,
    val apellidoMaternoDestinatario: String?,
    val costo: BigDecimal,
    var idClienteRemitente: Int,
    val idSucursalOrigen: Int,
    var idSucursalDestino: Int,
    val idColaborador: Int,
    val idUnidad: Int,
    val idDireccionDestino: Int,
    val idEstatus : Int


)
