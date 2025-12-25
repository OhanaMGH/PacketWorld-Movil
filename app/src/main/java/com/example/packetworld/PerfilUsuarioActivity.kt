package com.example.packetworld


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityPerfilUsuarioBinding
import com.example.packetworld.dominio.ColaboradorImp
import com.example.packetworld.poko.Colaborador
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class PerfilUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilUsuarioBinding
    private lateinit var colaboradorImp: ColaboradorImp
    private lateinit var colaborador: Colaborador
    private val REQUEST_EDITAR = 1
    private val REQUEST_FOTO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colaboradorImp = ColaboradorImp(this)

        //Colaborador recibido desde login
        val json = intent.getStringExtra("colaborador")
        if (json.isNullOrEmpty()) {
            Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        colaborador = Gson().fromJson(json, Colaborador::class.java)

        configurarListeners()
        cargarPerfil()
    }

    private fun configurarListeners() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            intent.putExtra("colaborador", Gson().toJson(colaborador))
            startActivityForResult(intent, REQUEST_EDITAR)
        }

        binding.ivCambiarFoto.setOnClickListener {
            seleccionarImagen()
        }
    }

    private fun cargarPerfil() {
        colaboradorImp.obtenerDatosPerfil(colaborador.idColaborador) { respuesta ->
            runOnUiThread {
                if (respuesta != null && !respuesta.error && respuesta.colaborador != null) {
                    val colaboradorPerfil = respuesta.colaborador
                    mostrarDatosPerfil(colaboradorPerfil)
                    cargarFoto()
                } else {
                    Toast.makeText(
                        this,
                        respuesta?.mensaje ?: "Error al cargar perfil",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun mostrarDatosPerfil(colaborador: Colaborador) {

        val nombreCompleto = "${colaborador.nombre} ${colaborador.apellidoPaterno} ${colaborador.apellidoMaterno ?: ""}".trim()

        binding.tvSubtitulo.text = nombreCompleto
        binding.tvNombre.text = nombreCompleto
        binding.tvCurp.text = colaborador.curp ?: "N/A"
        binding.tvCorreo.text = colaborador.correo ?: "N/A"
        binding.tvTelefono.text = colaborador.telefono ?: "N/A"
        binding.tvNumeroLicencia.text = colaborador.numeroLicencia ?: "N/A"
    }

    private fun cargarFoto() {
        colaboradorImp.obtenerFotoColaborador(colaborador.idColaborador) { colFoto ->
            runOnUiThread {
                if (!colFoto?.foto.isNullOrEmpty()) {
                    try {
                        //Base64 recibido
                        val base64Doble = colFoto.foto!!.replace("\\s".toRegex(), "")

                        //Primera decodificación → sigue siendo Base64
                        val base64SimpleBytes = Base64.decode(base64Doble, Base64.DEFAULT)
                        val base64Simple = String(base64SimpleBytes)

                        //Segunda decodificación → bytes reales de imagen
                        val imageBytes = Base64.decode(base64Simple, Base64.DEFAULT)

                        Log.d("PERFIL", "IMAGE BYTES = ${imageBytes.size}")

                        val bitmap = BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.size
                        )

                        if (bitmap != null) {
                            binding.ivPerfil.setImageBitmap(bitmap)
                        } else {
                            Log.e("PERFIL", "Bitmap NULL después de doble decode")
                            binding.ivPerfil.setImageResource(R.drawable.icon_usuario)
                        }

                    } catch (e: Exception) {
                        Log.e("PERFIL", "Error mostrando imagen", e)
                        binding.ivPerfil.setImageResource(R.drawable.icon_usuario)
                    }
                } else {
                    binding.ivPerfil.setImageResource(R.drawable.icon_usuario)
                }
            }
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_FOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_EDITAR && resultCode == Activity.RESULT_OK) {
            cargarPerfil()
        }

        if (requestCode == REQUEST_FOTO && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            val fotoBytes = uriToByteArray(uri)

            if (fotoBytes != null) {
                subirFoto(fotoBytes)
            } else {
                Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subirFoto(fotoBytes: ByteArray) {
        colaboradorImp.subirFotoColaborador(
            colaborador.idColaborador,
            fotoBytes
        ) { respuesta ->
            runOnUiThread {
                if (respuesta != null && !respuesta.error) {
                    Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
                    cargarFoto() // 🔹 solo recargamos la foto
                } else {
                    Toast.makeText(this, "Error al subir foto", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, baos)
            baos.toByteArray()
        } catch (e: Exception) {
            Log.e("PERFIL", "Error al convertir imagen", e)
            null
        }
    }
}
