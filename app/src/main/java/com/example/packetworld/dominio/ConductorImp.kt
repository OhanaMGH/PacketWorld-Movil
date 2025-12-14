package com.example.packetworld.dominio

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.packetworld.conexion.ConexionAPI
import com.example.packetworld.poko.RespuestaHTTP
import com.example.packetworld.poko.Colaborador
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.dto.Respuesta
import com.example.packetworld.util.Constantes
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class ConductorImp(private val context: Context) {

    private val gson = Gson()
    private val TAG = "LOGIN_API"
    private val contentTypeForm = "application/x-www-form-urlencoded"

    fun iniciarSesion(
        usuario: String,
        contrasenia: String,
        callback: (Colaborador?) -> Unit
    ) {

        val parametrosBody = "noPersonal=$usuario&password=$contrasenia"
        val url = "${Constantes.URL_API}colaborador/login"

        // 🔍 LOGS DE SALIDA
        Log.d(TAG, "================ LOGIN REQUEST ================")
        Log.d(TAG, "URL: $url")
        Log.d(TAG, "BODY: $parametrosBody")
        Log.d(TAG, "CONTENT-TYPE: $contentTypeForm")

        ConexionAPI.peticionBODY(
            context,
            url,
            "POST",
            parametrosBody,
            contentTypeForm
        ) { respuestaConexion: RespuestaHTTP ->

            // 🔍 LOGS DE RESPUESTA
            Log.d(TAG, "================ LOGIN RESPONSE ================")
            Log.d(TAG, "HTTP CODE: ${respuestaConexion.codigo}")
            Log.d(TAG, "RAW RESPONSE: ${respuestaConexion.contenido}")

            if (respuestaConexion.codigo == 200) {

                try {
                    Log.d(TAG, "Intentando parsear RSAutenticacionColaborador")

                    val respuestaLogin: RSAutenticacionColaborador =
                        gson.fromJson(
                            respuestaConexion.contenido,
                            RSAutenticacionColaborador::class.java
                        )

                    Log.d(TAG, "DTO PARSEADO: error=${respuestaLogin.error}")
                    Log.d(TAG, "MENSAJE: ${respuestaLogin.mensaje}")
                    Log.d(TAG, "COLABORADOR: ${respuestaLogin.colaborador}")

                    if (!respuestaLogin.error && respuestaLogin.colaborador != null) {

                        Log.d(TAG, "LOGIN EXITOSO")
                        callback(respuestaLogin.colaborador)

                    } else {

                        Log.e(TAG, "ERROR DE NEGOCIO")
                        Toast.makeText(
                            context,
                            respuestaLogin.mensaje ?: "Credenciales incorrectas",
                            Toast.LENGTH_LONG
                        ).show()

                        callback(null)
                    }

                } catch (e: JsonSyntaxException) {

                    Log.e(TAG, "JSON MAL FORMADO", e)

                    try {
                        val respuestaError: Respuesta =
                            gson.fromJson(respuestaConexion.contenido, Respuesta::class.java)

                        Toast.makeText(
                            context,
                            respuestaError.mensaje,
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (e2: Exception) {

                        Log.e(TAG, "NO SE PUDO PARSEAR RESPUESTA DE ERROR", e2)

                        Toast.makeText(
                            context,
                            Constantes.MSJ_ERROR_URL,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    callback(null)

                } catch (e: Exception) {

                    Log.e(TAG, "ERROR GENERAL AL PROCESAR LOGIN", e)

                    Toast.makeText(
                        context,
                        "Error interno del sistema",
                        Toast.LENGTH_LONG
                    ).show()

                    callback(null)
                }

            } else if (respuestaConexion.codigo == Constantes.ERROR_PETICION) {

                Log.e(TAG, "ERROR DE CONEXIÓN / RED")

                Toast.makeText(
                    context,
                    Constantes.MSJ_ERROR_PETICION,
                    Toast.LENGTH_LONG
                ).show()

                callback(null)

            } else {

                Log.e(TAG, "ERROR HTTP NO CONTROLADO: ${respuestaConexion.codigo}")

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
