package com.example.packetworld.dto

import com.example.packetworld.poko.Colaborador

data class RSAutenticacionColaborador(
    val error: Boolean,
    val mensaje: String,
    var colaborador: Colaborador?
)