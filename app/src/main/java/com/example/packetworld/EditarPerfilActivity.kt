package com.example.packetworld

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityEditarPerfilBinding
import com.example.packetworld.dominio.ColaboradorImp
import com.example.packetworld.poko.Colaborador
import com.google.gson.Gson

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var colaboradorImp: ColaboradorImp
    private lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colaboradorImp = ColaboradorImp(this)
        val jsonColaborador = intent.getStringExtra("colaborador")

        Log.d("EDITAR_PERFIL", "JSON recibido: $jsonColaborador")

        if (jsonColaborador == null) {
            Toast.makeText(
                this,
                "Error: No se recibió la información del colaborador.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        colaborador = Gson().fromJson(jsonColaborador, Colaborador::class.java)

        Log.d("EDITAR_PERFIL", "ID recibido: ${colaborador.idColaborador}")

        // Validación REAL (defensiva)
        if (colaborador.idColaborador <= 0) {
            Toast.makeText(
                this,
                "Error: ID de colaborador inválido.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        mostrarDatosActuales(colaborador)
        configurarListeners()
    }

    private fun configurarListeners() {

        binding.ivBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun mostrarDatosActuales(colaborador: Colaborador) {
        binding.etNombre.setText(colaborador.nombre)
        binding.etApellidoPaterno.setText(colaborador.apellidoPaterno)
        binding.etApellidoMaterno.setText(colaborador.apellidoMaterno)
        binding.etCurp.setText(colaborador.curp)
        binding.etCorreo.setText(colaborador.correo)
        binding.etTelefono.setText(colaborador.telefono)
        binding.etNumeroLicencia.setText(colaborador.numeroLicencia)
    }

    private fun guardarCambios() {
        val correo = binding.etCorreo.text.toString().trim()

        if (!esCorreoValido(correo)) {
            binding.etCorreo.error = "Correo electrónico inválido"
            binding.etCorreo.requestFocus()
            return
        }
        // 🔹 El ID ya viene en el objeto
        colaborador.nombre = binding.etNombre.text.toString()
        colaborador.apellidoPaterno = binding.etApellidoPaterno.text.toString()
        colaborador.apellidoMaterno = binding.etApellidoMaterno.text.toString()
        colaborador.curp = binding.etCurp.text.toString()
        colaborador.correo = correo
        colaborador.telefono = binding.etTelefono.text.toString()
        colaborador.numeroLicencia = binding.etNumeroLicencia.text.toString()


        Log.d(
            "EDITAR_PERFIL",
            "Enviando actualización | ID=${colaborador.idColaborador}"
        )

        colaboradorImp.actualizarPerfil(colaborador) { respuesta ->
            runOnUiThread {
                if (respuesta != null && !respuesta.error) {
                    Toast.makeText(
                        this,
                        respuesta.mensaje,
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        respuesta?.mensaje,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun esCorreoValido(correo: String): Boolean {
        return correo.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

}
