package com.example.medifinder_app.models

data class EspecialidadModel(
    val id: Int,
    val nombre: String,
    val especialidadMedicoIntermedia: List<String>
)