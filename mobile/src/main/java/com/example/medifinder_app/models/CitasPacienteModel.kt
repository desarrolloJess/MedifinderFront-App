package com.example.medifinder_app.models

data class CitasPacienteModel(
    val idPaciente: Int,
    val nombrePaciente: String,
    val apellidoPaciente: String,
    val citas: List<Cita>
) {
    data class Cita(
        val id: Int,
        val idMedico: Int,
        val fechaInicio: String,  // Utiliza String para manejar la fecha en formato ISO 8601
        val fechaFin: String,
        val descripcion: String?,
        val estatus: String,
        val fechaCancelacion: String?,
        val motivoCancelacion: String?,
        val nombreMedico: String,
        val apellidoMedico: String,
        val direccionMedico: String
    )
}