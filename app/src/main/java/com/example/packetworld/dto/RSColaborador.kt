package com.example.packetworld.dto

import com.example.packetworld.poko.Colaborador

data class RSColaborador(
    val error: Boolean,
    val mensaje: String,
    val colaborador: Colaborador?
)
