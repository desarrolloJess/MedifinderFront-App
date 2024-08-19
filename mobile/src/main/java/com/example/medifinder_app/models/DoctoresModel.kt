package com.example.medifinder_app.models
data class DoctoresModel(
    val idDoctor: Int,
    val nombreCompleto: String,
    val especialidades: List<String>,
    val direccion: String,
    val honorarios: Int
)