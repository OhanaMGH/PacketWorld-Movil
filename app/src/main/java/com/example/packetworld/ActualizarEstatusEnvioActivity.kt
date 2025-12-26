package com.example.packetworld

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityActualizarEstatusEnvioBinding
import com.example.packetworld.dominio.EnvioImp
import com.example.packetworld.dto.RSActualizarEstatus

import com.google.gson.Gson

class ActualizarEstatusEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActualizarEstatusEnvioBinding
    private lateinit var envioImp: EnvioImp
    private lateinit var gson: Gson
    private var numeroGuia: String = ""
    private val ID_DETENIDO = 4
    private val ID_CANCELADO = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarEstatusEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        envioImp = EnvioImp(this)

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
        val adapter = ArrayAdapter(this, R.layout.spinner_item, nombres)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spEstatus.adapter = adapter
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

            val estatus = RSActualizarEstatus(
                numeroGuia = numeroGuia,
                nuevoIdEstatus = selectedId,
                comentario = comentario,
                idColaborador = 1
            )

            envioImp.actualizarEstatusEnvio(estatus) { exito, mensaje ->
                runOnUiThread {
                    if (exito) {
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                        // Enviar comentario de vuelta a la actividad anterior
                        val resultIntent = Intent()
                        resultIntent.putExtra("COMENTARIO", comentario)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        // Botón regresar
        binding.ivBack.setOnClickListener { finish() }
    }
}
