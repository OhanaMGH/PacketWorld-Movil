package com.example.packetworld

import android.content.Intent
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
    private lateinit var jsonRespuestaLogin: String

    companion object {
        private const val REQUEST_DETALLE_ENVIO = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        envioImp = EnvioImp(this)

        mostrarInformacionColaborador()

        binding.ivUsuarioIcono.setOnClickListener {
            val intent = Intent(this@MainActivity, CuentaActivity::class.java)
            intent.putExtra("colaborador", jsonRespuestaLogin)
            startActivity(intent)
        }


    }



    private fun mostrarInformacionColaborador() {
        val json = intent.getStringExtra("colaborador")

        if (json != null) {
            jsonRespuestaLogin = json

            val respuesta = Gson().fromJson(
                json,
                RSAutenticacionColaborador::class.java
            )

            colaborador = respuesta.colaborador!!

            binding.tvSaludo.text = "Hola, ${colaborador.nombre}"
            cargarEnvios(colaborador.idColaborador)
        } else {
            Toast.makeText(this, "Error al recibir datos de sesión", Toast.LENGTH_LONG).show()
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

            // Colores según estatus
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

                "Cancelado" -> {
                    tvEstado.text = "Cancelado"
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
                    "Cargando detalles de : ${envio.numeroGuia}",
                    Toast.LENGTH_SHORT
                ).show()

                envioImp.obtenerDetalleEnvio(envio.numeroGuia) { detalleEnvio ->
                    if (detalleEnvio != null) {
                        val gson = Gson()
                        val envioJson = gson.toJson(detalleEnvio)
                        val intent = Intent(this, DetalleEnvioActivity::class.java)
                        intent.putExtra("ENVIO_SELECCIONADO", envioJson)
                        startActivityForResult(intent, REQUEST_DETALLE_ENVIO)
                    } else {
                        Toast.makeText(
                            this,
                            "No se pudieron cargar los detalles del envío.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DETALLE_ENVIO && resultCode == RESULT_OK) {
            cargarEnvios(colaborador.idColaborador) // recargar lista y cuadros de estatus
        }
    }
}
