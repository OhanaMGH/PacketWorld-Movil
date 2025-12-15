package com.example.packetworld

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.conexion.ConexionAPI
import com.example.packetworld.databinding.ActivityActualizarEstatusEnvioBinding
import com.example.packetworld.dto.RSActualizarEstatus
import com.example.packetworld.util.Constantes
import com.google.gson.Gson

class ActualizarEstatusEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActualizarEstatusEnvioBinding
    private lateinit var gson: Gson
    private var numeroGuia: String = ""

    private val ID_DETENIDO = 4
    private val ID_CANCELADO = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarEstatusEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gson = Gson()

        // Obtener número de guía desde el Intent
        numeroGuia = intent.getStringExtra("NUMERO_GUIA") ?: ""
        binding.tvGuia.text = "Guía: $numeroGuia"

        // Spinner simple con estatus
        val estatusList = listOf(
            "Recibido en sucursal" to 1,
            "Procesado" to 2,
            "En tránsito" to 3,
            "Detenido" to 4,
            "Entregado" to 5,
            "Cancelado" to 6
        )
        val nombres = estatusList.map { it.first }
        binding.spEstatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)


        binding.spEstatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val idEstatus = estatusList[position].second
                val nombreEstatus = estatusList[position].first

                // Siempre mostramos el card y layout
                binding.cardComentario.visibility = View.VISIBLE
                binding.layoutMensaje.visibility = View.VISIBLE

                // Cambiar textos según si es obligatorio o no
                binding.tvNuevoEstatusInfo.text = "Nuevo estatus: $nombreEstatus"
                if (idEstatus == ID_DETENIDO || idEstatus == ID_CANCELADO) {
                    binding.tvRequerimientoComentario.text =
                        "ⓘ Se requiere proporcionar un comentario para el estatus '$nombreEstatus'."
                } else {
                    binding.tvRequerimientoComentario.text =
                        "ⓘ El comentario es opcional para este estatus."
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        // Botón Guardar cambios
        binding.btnGuardarCambios.setOnClickListener {
            val comentario = binding.etComentario.text.toString()
            val selectedId = estatusList[binding.spEstatus.selectedItemPosition].second

            if ((selectedId == ID_DETENIDO || selectedId == ID_CANCELADO) && comentario.isBlank()) {
                Toast.makeText(this, "Se requiere un comentario para el estatus seleccionado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val solicitud = RSActualizarEstatus(numeroGuia, selectedId, comentario, 1)
            val jsonSolicitud = gson.toJson(solicitud)

            ConexionAPI.peticionBODY(
                context = this,
                url = "${Constantes.URL_API}envio/estatus",
                metodoHTTP = "PUT",
                parametros = jsonSolicitud,
                contentType = "application/json"
            ) { respuesta ->
                if (respuesta.codigo == 200) {
                    Toast.makeText(this, "Estatus actualizado correctamente", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${respuesta.contenido}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Botón regresar
        binding.ivBack.setOnClickListener { finish() }
    }
}
