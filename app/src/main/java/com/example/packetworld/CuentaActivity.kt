package com.example.packetworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityCuentaBinding
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.google.gson.Gson

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private lateinit var jsonLogin: String
    private lateinit var respuestaLogin: RSAutenticacionColaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonLogin = intent.getStringExtra("colaborador") ?: ""

        if (jsonLogin.isEmpty()) {
            Toast.makeText(this, "No se recibió información de sesión", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        //convertir el json a objeto (RS)
        respuestaLogin = try {
            Gson().fromJson(jsonLogin, RSAutenticacionColaborador::class.java)
             } catch (e: Exception) {
                Toast.makeText(this, "Error al procesar sesión", Toast.LENGTH_LONG).show()
                finish()
                return
            }

        if (respuestaLogin.colaborador == null) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        configurarListeners()
    }

    private fun configurarListeners() {

        binding.cardDatosPersonales.setOnClickListener {

            Log.d("CUENTA", "Click en Datos Personales")

            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            intent.putExtra(
                "colaborador",
                Gson().toJson(respuestaLogin.colaborador)
            )
            startActivity(intent)
        }

        binding.cardSeguridad.setOnClickListener {
            val intent = Intent(this, CambioPasswordActivity::class.java)
            intent.putExtra(
                "colaborador",
                Gson().toJson(respuestaLogin.colaborador)
            )
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
