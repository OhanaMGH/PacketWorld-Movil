package com.example.packetworld
import android.util.Log


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.MainActivity // Tu pantalla principal
import com.example.packetworld.databinding.ActivityLoginBinding
import com.example.packetworld.dominio.ConductorImp // Importamos la implementación del Repositorio
import com.example.packetworld.poko.Colaborador // Importamos el POKO

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var conductorImp: ConductorImp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el ConductorImp pasándole el contexto
        conductorImp = ConductorImp(this)


        binding.btnLogin.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        if (sonCamposValidos()) {

            val numPersonal = binding.etNumPersonal.text.toString()
            val password = binding.etPassword.text.toString()

            // Llamada al ConductorImp (Delegación de la lógica de negocio y red)
            conductorImp.iniciarSesion(numPersonal, password) { colaborador ->


                if (colaborador != null) {

                    Toast.makeText(
                        this,
                        "Bienvenido(a) ${colaborador.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()

                    irPantallaPrincipal()

                }

            }

        }
    }

    private fun sonCamposValidos(): Boolean {
        // La lógica de validación de UI se mantiene aquí
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

    private fun irPantallaPrincipal() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Es crucial llamar a finish() para que el usuario no pueda volver al login con el botón 'Atrás'
        finish()
    }
}