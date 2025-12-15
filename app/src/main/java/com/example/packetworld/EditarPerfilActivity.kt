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

    private var idColaborador: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colaboradorImp = ColaboradorImp(this)

        // 1️⃣ Obtener el ID del colaborador
        idColaborador = intent.getIntExtra("ID_COLABORADOR", -1)

        // 2️⃣ Obtener el Colaborador completo (incluyendo contraseña)
        val jsonColaborador = intent.getStringExtra("colaborador_json")
        if (jsonColaborador != null) {
            colaborador = Gson().fromJson(jsonColaborador, Colaborador::class.java)
        }

        configurarListeners()
        cargarDatosActuales()
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

    private fun cargarDatosActuales() {

        if (idColaborador <= 0) {
            Toast.makeText(this, "ID de colaborador inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        colaboradorImp.obtenerDatosPerfil(idColaborador) { respuestaRS ->

            runOnUiThread {
                if (respuestaRS != null && !respuestaRS.error && respuestaRS.colaborador != null) {

                    colaborador = respuestaRS.colaborador

                    binding.etNombre.setText(colaborador.nombre)
                    binding.etApellidoPaterno.setText(colaborador.apellidoPaterno)
                    binding.etApellidoMaterno.setText(colaborador.apellidoMaterno)
                    binding.etCurp.setText(colaborador.curp)
                    binding.etCorreo.setText(colaborador.correo)
                    binding.etTelefono.setText(colaborador.telefono)
                    binding.etNumeroLicencia.setText(colaborador.numeroLicencia)

                } else {
                    Toast.makeText(
                        this,
                        respuestaRS?.mensaje ?: "No se pudieron cargar los datos",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun guardarCambios() {

        colaborador.idColaborador = idColaborador
        colaborador.nombre = binding.etNombre.text.toString()
        colaborador.apellidoPaterno = binding.etApellidoPaterno.text.toString()
        colaborador.apellidoMaterno = binding.etApellidoMaterno.text.toString()
        colaborador.curp = binding.etCurp.text.toString()
        colaborador.correo = binding.etCorreo.text.toString()
        colaborador.telefono = binding.etTelefono.text.toString()
        colaborador.numeroLicencia = binding.etNumeroLicencia.text.toString()


        Log.d("EDITAR_PERFIL", "ID: ${colaborador.idColaborador}")
        Log.d("EDITAR_PERFIL", "Nombre: ${colaborador.nombre}")
        Log.d("EDITAR_PERFIL", "AP: ${colaborador.apellidoPaterno}")
        Log.d("EDITAR_PERFIL", "AM: ${colaborador.apellidoMaterno}")
        Log.d("EDITAR_PERFIL", "CURP: ${colaborador.curp}")
        Log.d("EDITAR_PERFIL", "Correo: ${colaborador.correo}")
        Log.d("EDITAR_PERFIL", "Teléfono: ${colaborador.telefono}")
        Log.d("EDITAR_PERFIL", "Licencia: ${colaborador.numeroLicencia}")
        Log.d("EDITAR_PERFIL", "Password: ${colaborador.password}")
        Log.d("EDITAR_PERFIL", "Foto: ${colaborador.foto}")

        val password = binding.etPassword.text.toString()
        colaborador.password = if (password.isNotEmpty()) password else colaborador.password

        // 🔑 Backend espera el campo
        colaborador.foto = null

        colaboradorImp.actualizarPerfil(colaborador) { respuesta ->
            runOnUiThread {
                if (respuesta != null && !respuesta.error) {
                    Toast.makeText(this, respuesta.mensaje, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        respuesta?.mensaje ?: "Error en operación de colaborador",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


}
