package com.example.packetworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityDetalleEnvioBinding
import com.example.packetworld.dto.RSEnvioDetalle
import com.example.packetworld.poko.Paquete
import com.example.packetworld.dominio.EnvioImp
import com.google.gson.Gson

class DetalleEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEnvioBinding
    private lateinit var envioImp: EnvioImp
    private var numeroGuia: String = ""
    private lateinit var detalleEnvioActual: RSEnvioDetalle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        envioImp = EnvioImp(this)

        // Recuperar envío completo del Intent
        val envioJson = intent.getStringExtra("ENVIO_SELECCIONADO") ?: ""
        val gson = Gson()
        detalleEnvioActual = gson.fromJson(envioJson, RSEnvioDetalle::class.java)
        numeroGuia = detalleEnvioActual.numeroGuia

        // Mostrar detalle inicial
        mostrarDetallesEnvio(detalleEnvioActual)

        binding.ivBack.setOnClickListener { finish() }

        binding.btnActualizarEstado.setOnClickListener {
            val intent = Intent(this, ActualizarEstatusEnvioActivity::class.java)
            intent.putExtra("NUMERO_GUIA", numeroGuia)
            startActivityForResult(intent, 1)
        }
    }


    private fun cargarDetalleEnvio() {
        if (numeroGuia.isEmpty()) return

        envioImp.obtenerDetalleEnvio(numeroGuia) { detalle ->
            runOnUiThread {
                if (detalle != null) {
                    detalleEnvioActual = detalle
                    mostrarDetallesEnvio(detalle)

                    //Mostrar comentario si el estatus indica Detenido o Cancelado
                    val requiereComentario = when {
                        detalle.idEstatus == 4 || detalle.idEstatus == 6 -> true
                        detalle.estatus.contains("Cancelado", ignoreCase = true) -> true
                        detalle.estatus.contains("Detenido", ignoreCase = true) -> true
                        else -> false
                    }

                    if (requiereComentario) {
                        envioImp.obtenerUltimoComentario(detalle.idEnvio) { comentario ->
                            runOnUiThread {
                                if (!comentario.isNullOrBlank()) {
                                    binding.tvComentario.text = "Comentario (motivo): $comentario"
                                    binding.tvComentario.visibility = View.VISIBLE
                                } else {
                                    binding.tvComentario.visibility = View.GONE
                                }
                            }
                        }
                    } else {
                        Log.d("DETALLE_ENVIO", "Estatus no requiere comentario, ocultando TextView")
                        binding.tvComentario.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this, "No se pudieron cargar los detalles.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun mostrarDetallesEnvio(envio: RSEnvioDetalle) {
        binding.tvCodigoEnvio.text = envio.numeroGuia
        binding.tvEstatus.text = envio.estatus
        binding.tvFechaEntrega.text = "Fecha de Entrega: ${envio.fechaEntrega}"
        binding.tvSucursalOrigen.text = envio.sucursalOrigen

        val nombreDest = "${envio.nombreDestinatario} ${envio.apellidoPaternoDestinatario} ${envio.apellidoMaternoDestinatario ?: ""}".trim()
        binding.tvNombreCompletoDestinatario.text = nombreDest

        binding.tvDireccionCalle.text = "${envio.dirCalle} #${envio.dirNumero}"
        binding.tvDireccionColonia.text = "Colonia ${envio.dirColonia}"
        binding.tvDireccionCiudad.text = "${envio.dirCiudad}, ${envio.dirEstado}"
        binding.tvDireccionCP.text = "C.P. ${envio.dirCP}"

        val nombreCliente = "${envio.clienteNombre} ${envio.clienteApellidoPaterno} ${envio.clienteApellidoMaterno ?: ""}".trim()
        binding.tvNombreCompletoCliente.text = nombreCliente
        binding.tvNumeroCliente.text = envio.clienteTelefono
        binding.tvCorreoCliente.text = envio.clienteCorreo

        // Paquetes
        if (!envio.paquetes.isNullOrEmpty()) {
            val p1 = envio.paquetes!![0]
            binding.tvPaquete1Desc.text = formatPaquete(p1)
            binding.tvPaquete1Desc.visibility = View.VISIBLE

            if (envio.paquetes!!.size > 1) {
                val p2 = envio.paquetes!![1]
                binding.tvPaquete2Desc.text = formatPaquete(p2)
                binding.tvPaquete2Desc.visibility = View.VISIBLE
            } else {
                binding.tvPaquete2Desc.visibility = View.GONE
            }
        } else {
            binding.tvPaquete1Desc.text = "No se especificó el contenido del envío."
            binding.tvPaquete2Desc.visibility = View.GONE
        }
    }

    private fun formatPaquete(paquete: Paquete): String {
        val pesoDouble = paquete.peso.toDoubleOrNull() ?: 0.0
        return "• ${paquete.descripcion} (${"%.2f".format(pesoDouble)} kg) - ${paquete.alto}x${paquete.ancho}x${paquete.profundidad} cm"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDetalleEnvio()
            setResult(RESULT_OK)
        }
    }

}
