package com.example.packetworld

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityCambioPasswordBinding
import com.example.packetworld.dominio.ColaboradorImp
import com.example.packetworld.poko.Colaborador
import com.google.gson.Gson

class CambioPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCambioPasswordBinding
    private lateinit var colaboradorImp: ColaboradorImp
    private lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCambioPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colaboradorImp = ColaboradorImp(this)

        val jsonColaborador = intent.getStringExtra("colaborador")


        if (jsonColaborador.isNullOrEmpty()) {
            Toast.makeText(this, "No se recibió información del colaborador", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        colaborador = Gson().fromJson(jsonColaborador, Colaborador::class.java)

        if (colaborador.idColaborador <= 0) {
            Toast.makeText(this, "ID de colaborador inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        configurarListeners()
    }

    private fun configurarListeners() {

        binding.btnCambiarPassword.setOnClickListener {
            cambiarPassword()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun cambiarPassword() {

        val passwordActual = binding.etPasswordActual.text.toString()
        val passwordNueva = binding.etPasswordNueva.text.toString()

        if (passwordActual.isEmpty() || passwordNueva.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(
            "CAMBIO_PASSWORD",
            "Enviando cambio password | ID=${colaborador.idColaborador}"
        )

        colaboradorImp.cambiarPassword(colaborador.idColaborador, passwordActual, passwordNueva) { respuesta ->
            runOnUiThread {
                if (respuesta != null && !respuesta.error) {
                    Toast.makeText(this, respuesta.mensaje, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        respuesta?.mensaje ?: "Error al cambiar contraseña",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
