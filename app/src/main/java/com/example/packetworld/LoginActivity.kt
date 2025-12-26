package com.example.packetworld

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityLoginBinding
import com.example.packetworld.dominio.ColaboradorImp
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.poko.Colaborador
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var colaboradorImp: ColaboradorImp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colaboradorImp = ColaboradorImp(this)

        binding.btnLogin.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {

        if (!sonCamposValidos()) return

        val numPersonal = binding.etNumPersonal.text.toString()
        val password = binding.etPassword.text.toString()

        colaboradorImp.iniciarSesion(numPersonal, password) { colaborador: Colaborador? ->

            if (colaborador != null) {

                // Limpiar la foto para no enviar demasiado peso
                val colaboradorSinFoto = colaborador.copy(foto = null)

                Toast.makeText(
                    this,
                    "Bienvenido(a) ${colaborador.nombre}",
                    Toast.LENGTH_SHORT
                ).show()

                val respuestaLogin = RSAutenticacionColaborador(
                    error = false,
                    mensaje = "Login correcto",
                    colaborador = colaboradorSinFoto
                )

                val gson = Gson()
                val jsonRespuesta = gson.toJson(respuestaLogin)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("colaborador", jsonRespuesta)
                startActivity(intent)

                finish()
            }

        }
    }

    private fun sonCamposValidos(): Boolean {
        var valido = true

        if (binding.etNumPersonal.text.isEmpty()) {
            binding.etNumPersonal.error = "Número de personal obligatorio"
            valido = false
        }

        if (binding.etPassword.text.isEmpty()) {
            binding.etPassword.error = "Contraseña obligatoria"
            valido = false
        }

        return valido
    }
}
