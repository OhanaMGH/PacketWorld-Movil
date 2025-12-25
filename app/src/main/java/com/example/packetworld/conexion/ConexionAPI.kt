// Archivo: com/example/packetworld/conexion/ConexionAPI.kt (Actualizado)

package com.example.packetworld.conexion

import android.content.Context
import com.koushikdutta.ion.Ion
import com.koushikdutta.ion.builder.Builders
import com.example.packetworld.poko.RespuestaHTTP
import com.example.packetworld.util.Constantes

object ConexionAPI {

    /**
     * Realiza una petición GET asíncrona usando Ion.
     * @param callback Función lambda que recibe el objeto RespuestaConexion.
     */
    fun peticionGET(context: Context, url: String, callback: (RespuestaHTTP) -> Unit) {
        Ion.with(context)
            .load("GET", url)
            .asString()
            .setCallback { e, result ->
                val respuesta = RespuestaHTTP()

                if (e == null && result != null) {
                    // Si no hay excepción de red, asumimos éxito (200)
                    respuesta.codigo = 200
                    respuesta.contenido = result
                } else {
                    // Error de red/conexión
                    respuesta.contenido= Constantes.MSJ_ERROR_PETICION

                }
                callback(respuesta)
            }
    }

    /**
     * Realiza peticiones asíncronas que requieren un cuerpo (POST/PUT).
     * @param callback Función lambda que recibe el objeto RespuestaConexion.
     */
    fun peticionBODY(
        context: Context,
        url: String,
        metodoHTTP: String,
        parametros: String,
        contentType: String,
        callback: (RespuestaHTTP) -> Unit
    ) {
        val request: Builders.Any.B = Ion.with(context).load(metodoHTTP, url)

        request.setHeader("Content-Type", contentType)
            .setStringBody(parametros)
            .asString()
            .setCallback { e, result ->
                val respuesta = RespuestaHTTP()

                if (e == null && result != null) {
                    // Si no hay excepción de red, asumimos éxito (200)
                    respuesta.codigo = 200
                    respuesta.contenido = result
                } else {
                    // Error de red/conexión
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = e?.message ?: "Error de red desconocido."
                }
                callback(respuesta)
            }
    }

    fun peticionBODYBytes(
        context: Context,
        url: String,
        metodoHTTP: String,
        parametros: ByteArray,
        contentType: String,
        callback: (RespuestaHTTP) -> Unit
    ) {
        val request: Builders.Any.B = Ion.with(context).load(metodoHTTP, url)

        request.setHeader("Content-Type", contentType)
            .setByteArrayBody(parametros)
            .asString()
            .setCallback { e, result ->
                val respuesta = RespuestaHTTP()

                if (e == null && result != null) {
                    respuesta.codigo = 200
                    respuesta.contenido = result
                } else {
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = e?.message ?: "Error de red desconocido."
                }
                callback(respuesta)
            }
    }

}