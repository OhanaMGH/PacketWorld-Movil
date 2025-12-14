package com.example.packetworld

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworld.databinding.ActivityMainBinding
import com.example.packetworld.dto.RSAutenticacionColaborador
import com.example.packetworld.poko.Colaborador
import com.example.packetworld.poko.Envio
import com.example.packetworld.util.Constantes
import com.google.gson.Gson
import com.koushikdutta.ion.Ion

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var colaborador: Colaborador

    private lateinit var envio : Envio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Contenedores de alto nivel

    }

}