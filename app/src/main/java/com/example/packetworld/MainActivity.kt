package com.example.packetworld

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityMainBinding
import com.example.packetworld.dominio.EnvioImp
import com.example.packetworld.dto.RSEnvioLista
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.poko.Colaborador
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var envioImp: EnvioImp
    private lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        envioImp = EnvioImp(this)

        mostrarInformacionColaborador()
    }

    private fun mostrarInformacionColaborador() {
        try {
            val jsonColaborador = intent.getStringExtra("colaborador")

            if (jsonColaborador != null) {
                val gson = Gson()

                val respuestaLogin = gson.fromJson(
                    jsonColaborador,
                    RSAutenticacionColaborador::class.java
                )

                colaborador = respuestaLogin.colaborador!!

                // Saludo
                binding.tvSaludo.text = "Hola, ${colaborador.nombre}"

                // Cargar envíos
                cargarEnvios(colaborador.idColaborador)
            } else {
                Toast.makeText(
                    this,
                    "No se recibió información del colaborador",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error al cargar la información del colaborador",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun cargarEnvios(idColaborador: Int) {
        Toast.makeText(this, "Cargando envíos...", Toast.LENGTH_SHORT).show()
        binding.layoutEnvios.removeAllViews()

        envioImp.obtenerEnvios(idColaborador) { listaEnvios: List<RSEnvioLista>? ->

            if (listaEnvios != null) {
                if (listaEnvios.isNotEmpty()) {
                    mostrarDatosDeEnvios(listaEnvios)
                } else {
                    sinDatos("No tienes envíos asignados.")
                }
            } else {
                sinDatos("Error al cargar los envíos.")
            }
        }
    }


    private fun mostrarDatosDeEnvios(listaEnvios: List<RSEnvioLista>) {

        // Resumen
        val totalCompletados = listaEnvios.count { it.estatus == "Entregado" }

        try {
            binding.layoutResumenEnvios.tvEnviosHoyTotal.text = listaEnvios.size.toString()
            binding.layoutResumenEnvios.tvCompletadosTotal.text = totalCompletados.toString()
        } catch (e: Exception) {
            binding.layoutResumenEnvios.root
                .findViewById<TextView>(R.id.tv_envios_hoy_total)
                .text = listaEnvios.size.toString()

            binding.layoutResumenEnvios.root
                .findViewById<TextView>(R.id.tv_completados_total)
                .text = totalCompletados.toString()
        }

        // Lista dinámica
        val contenedor = binding.layoutEnvios
        val inflater = LayoutInflater.from(this)

        listaEnvios.forEach { envio ->
            val itemView = inflater.inflate(R.layout.item_envio, contenedor, false)

            val tvCodigoEnvio = itemView.findViewById<TextView>(R.id.tvCodigoEnvio)
            val tvEstado = itemView.findViewById<TextView>(R.id.tvEstado)
            val tvDireccion = itemView.findViewById<TextView>(R.id.tvDireccion)

            tvCodigoEnvio.text = envio.numeroGuia
            tvDireccion.text = envio.direccionDestino

            // ===== COLORES SEGÚN ESTATUS =====
            when (envio.estatus) {

                "Entregado" -> {
                    tvEstado.text = "Entregado"
                    tvEstado.setTextColor(getColor(android.R.color.white))
                    tvEstado.setBackgroundColor(getColor(android.R.color.holo_green_dark))
                }

                "En tránsito" -> {
                    tvEstado.text = "En tránsito"
                    tvEstado.setTextColor(getColor(android.R.color.white))
                    tvEstado.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
                }

                "Detenido" -> {
                    tvEstado.text = "Detenido"
                    tvEstado.setTextColor(getColor(android.R.color.white))
                    tvEstado.setBackgroundColor(getColor(android.R.color.holo_red_dark))
                }

                else -> {
                    tvEstado.text = envio.estatus
                    tvEstado.setTextColor(getColor(android.R.color.black))
                    tvEstado.setBackgroundColor(getColor(android.R.color.darker_gray))
                }
            }

            itemView.setOnClickListener {
                Toast.makeText(
                    this,
                    "Guía seleccionada: ${envio.numeroGuia}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            contenedor.addView(itemView)
        }
    }


    private fun sinDatos(mensaje: String) {
        val tvMensaje = TextView(this)
        tvMensaje.text = mensaje
        tvMensaje.setPadding(0, 50, 0, 50)
        tvMensaje.textSize = 16f
        tvMensaje.setTextColor(getColor(android.R.color.darker_gray))

        binding.layoutEnvios.addView(tvMensaje)
    }
}
