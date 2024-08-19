package com.example.medifinder_app.models
data class DatosPaciente(
    val id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasena: String,
    val telefono: String,
    val fechaNacimiento: String,
    val sexo: String,
    val estatus: String,
    val cita: List<Any> = emptyList(),
    val pacientesAsignados: List<Any> = emptyList()
)