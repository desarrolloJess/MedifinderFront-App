package com.example.medifinder_app.models

data class RegistroCita (
    val idPaciente: Int?,
    val idMedico: Int?,
    val fechaInicio: String?,
    val fechaFin: String?,
    val descripcion: String? = null,
    val fechaCancelacion: String? = null,
    val motivoCancelacion: String? = null
)