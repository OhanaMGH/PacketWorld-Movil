package com.example.packetworld.dominio

import android.content.Context
import android.widget.Toast
import com.example.packetworld.conexion.ConexionAPI
import com.example.packetworld.poko.RespuestaHTTP
import com.example.packetworld.poko.Colaborador
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.dto.Respuesta
import com.example.packetworld.util.Constantes
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class ColaboradorImp(private val context: Context) {

    private val gson = Gson()
    private val contentTypeForm = "application/x-www-form-urlencoded"

    // Rol permitido
    private val ROL_REQUERIDO = "Conductor"

    fun iniciarSesion(
        usuario: String,
        contrasenia: String,
        callback: (Colaborador?) -> Unit
    ) {

        val parametrosBody = "noPersonal=$usuario&password=$contrasenia"
        val url = "${Constantes.URL_API}colaborador/login"

        ConexionAPI.peticionBODY(
            context,
            url,
            "POST",
            parametrosBody,
            contentTypeForm
        ) { respuestaConexion: RespuestaHTTP ->

            if (respuestaConexion.codigo == 200) {
                try {
                    val respuestaLogin: RSAutenticacionColaborador =
                        gson.fromJson(
                            respuestaConexion.contenido,
                            RSAutenticacionColaborador::class.java
                        )

                    val colaborador = respuestaLogin.colaborador

                    if (!respuestaLogin.error && colaborador != null) {

                        // Validación de rol
                        if (colaborador.nombreRol == ROL_REQUERIDO) {
                            callback(colaborador)
                        } else {
                            Toast.makeText(
                                context,
                                "Acceso denegado. Rol '${colaborador.nombreRol}' no permitido.",
                                Toast.LENGTH_LONG
                            ).show()
                            callback(null)
                        }

                    } else {
                        Toast.makeText(
                            context,
                            respuestaLogin.mensaje ?: "Credenciales incorrectas",
                            Toast.LENGTH_LONG
                        ).show()
                        callback(null)
                    }

                } catch (e: JsonSyntaxException) {
                    try {
                        val respuestaError: Respuesta =
                            gson.fromJson(respuestaConexion.contenido, Respuesta::class.java)

                        Toast.makeText(
                            context,
                            respuestaError.mensaje,
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (e2: Exception) {
                        Toast.makeText(
                            context,
                            Constantes.MSJ_ERROR_URL,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    callback(null)

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error interno del sistema",
                        Toast.LENGTH_LONG
                    ).show()
                    callback(null)
                }

            } else if (respuestaConexion.codigo == Constantes.ERROR_PETICION) {
                Toast.makeText(
                    context,
                    Constantes.MSJ_ERROR_PETICION,
                    Toast.LENGTH_LONG
                ).show()
                callback(null)

            } else {
                Toast.makeText(
                    context,
                    Constantes.MSJ_ERROR_URL,
                    Toast.LENGTH_LONG
                ).show()
                callback(null)
            }
        }
    }
}
