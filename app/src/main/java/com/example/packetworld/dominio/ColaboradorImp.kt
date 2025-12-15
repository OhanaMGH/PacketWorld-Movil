package com.example.packetworld.dominio

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.packetworld.conexion.ConexionAPI
import com.example.packetworld.poko.RespuestaHTTP
import com.example.packetworld.poko.Colaborador
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.dto.RSColaborador
import com.example.packetworld.dto.Respuesta
import com.example.packetworld.util.Constantes
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class ColaboradorImp(private val context: Context) {
    private val gson = Gson()
    private val contentTypeForm = "application/x-www-form-urlencoded"
    private val contentTypeJson = "application/json"
    private val ROL_REQUERIDO = "Conductor"

    fun iniciarSesion(usuario: String, contrasenia: String, callback: (Colaborador?) -> Unit) {

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


    fun obtenerDatosPerfil(idColaborador: Int, callback: (RSColaborador?) -> Unit){
        val url = "${Constantes.URL_API}colaborador/perfil/obtener/$idColaborador"
        ConexionAPI.peticionGET(context, url) { respuestaConexion: RespuestaHTTP ->
            if (respuestaConexion.codigo == 200) {
                try {
                    val type = object : TypeToken<RSColaborador>() {}.type
                    val respuestaRS: RSColaborador = gson.fromJson(respuestaConexion.contenido, type)
                    if (respuestaRS.error) {
                        // El servidor devolvió un error lógico (ej. Colaborador no encontrado)
                        Toast.makeText(context, respuestaRS.mensaje, Toast.LENGTH_LONG).show()
                        callback(null)
                    } else {
                        // Éxito: Devolvemos el objeto RSColaborador completo
                        callback(respuestaRS)
                    }

                } catch (e: JsonSyntaxException) {
                    Toast.makeText(
                        context,
                        "Error al procesar la respuesta del servidor (JSON inválido).",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                    callback(null)

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error desconocido al procesar la respuesta.",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                    callback(null)
                }

            } else {
                // Error de conexión HTTP (ej. 404, 500, etc.)
                Toast.makeText(
                    context,
                    "Error de conexión con el servidor: Código ${respuestaConexion.codigo}",
                    Toast.LENGTH_LONG
                ).show()
                callback(null)
            }
        }

    }

    fun actualizarPerfil(colaborador: Colaborador, callback: (RSColaborador?) -> Unit) {

        val gson = Gson()
        val json = gson.toJson(colaborador)

        Log.d("API_COLABORADOR", "JSON ENVIADO:")
        Log.d("API_COLABORADOR", gson.toJson(colaborador))


        ConexionAPI.peticionBODY(
            context = context,
            url = "${Constantes.URL_API}colaborador/perfil/actualizar",
            metodoHTTP = "PUT",
            parametros = json,
            contentType = "application/json"
        ) { respuestaHTTP ->
            Log.d("API_COLABORADOR", "Respuesta cruda: ${respuestaHTTP.contenido}")

            if (respuestaHTTP.codigo == 200) {
                val rs = gson.fromJson(respuestaHTTP.contenido, RSColaborador::class.java)
                callback(rs)

                Log.d("API_COLABORADOR", "Respuesta cruda: ${respuestaHTTP.contenido}")

            } else {
                callback(null)
            }
        }
    }


}
