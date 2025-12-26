package com.example.packetworld.dominio

import android.content.Context
import android.widget.Toast
import com.example.packetworld.conexion.ConexionAPI
import com.example.packetworld.poko.RespuestaHTTP
import com.example.packetworld.dto.RSEnvioLista
import com.example.packetworld.util.Constantes
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken


class EnvioImp(private val context: Context) {

    private val gson = Gson()
    private val TAG = "ENVIOS_API"

    fun obtenerEnvios(
        idColaborador: Int,
        callback: (List<RSEnvioLista>?) -> Unit
    ) {
        val url = "${Constantes.URL_API}envio/porConductor/$idColaborador"

        ConexionAPI.peticionGET(context, url) { respuestaConexion: RespuestaHTTP ->

            if (respuestaConexion.codigo == 200) {

                try {

                    val type = object : TypeToken<List<RSEnvioLista>>() {}.type
                    val listaEnvios: List<RSEnvioLista> = gson.fromJson(respuestaConexion.contenido, type)

                    // Devolvemos la lista exitosa
                    callback(listaEnvios)

                } catch (e: JsonSyntaxException) {
                    Toast.makeText(context, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                    callback(null)

                } catch (e: Exception) {
                    Toast.makeText(context, "Error desconocido en la respuesta.", Toast.LENGTH_LONG).show()
                    callback(null)
                }

            } else {
                Toast.makeText(context, "Error del servidor: Código ${respuestaConexion.codigo}", Toast.LENGTH_LONG).show()
                callback(null)
            }
        }
    }

    // NOTA: Aquí se agregarían otros métodos (obtenerDetalleEnvio, actualizarEstatusEnvio)

        // Endpoint de actualización de estatus
        val url = "${Constantes.URL_API}envio/estatus"

        // Convertir el RS a JSON
        val jsonSolicitud = gson.toJson(solicitud)

        Log.d(TAG, "JSON a enviar: $jsonSolicitud")

        // Petición POST (usando peticionBODY)
        ConexionAPI.peticionBODY(
            context = context,
            url = url,
            metodoHTTP = "PUT",
            parametros = jsonSolicitud,
            contentType = "application/json"
        ) { respuestaConexion ->
            if (respuestaConexion.codigo == 200) {
                callback(true, "Estatus actualizado correctamente")
            } else {
                Log.e(TAG, "Error al actualizar estatus: ${respuestaConexion.contenido}")
                callback(false, respuestaConexion.contenido ?: "Error desconocido al actualizar estatus.")
            }
        }
    }
    fun obtenerUltimoComentario(
        idEnvio: Int,
        callback: (String?) -> Unit
    ) {
        val url = "${Constantes.URL_API}envio/ultimoComentario/$idEnvio"

        ConexionAPI.peticionGET(context, url) { respuestaConexion ->
            if (respuestaConexion.codigo == 200) {
                val comentario = if (respuestaConexion.contenido.isNullOrEmpty()) null
                else respuestaConexion.contenido
                callback(comentario)
            } else {
                Log.e("ENVIOS_API", "Error al obtener último comentario: ${respuestaConexion.contenido}")
                callback(null)
            }
        }
}