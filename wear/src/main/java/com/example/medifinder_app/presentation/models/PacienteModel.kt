package com.example.medifinder_app.presentation.models

import com.google.gson.annotations.SerializedName
data class PacienteModel(
    @SerializedName("idPaciente") val idPaciente: Int,
    @SerializedName("nombrePaciente") val nombrePaciente: String,
    @SerializedName("apellidoPaciente") val apellidoPaciente: String,
    @SerializedName("citas") val citas: List<CitaModel>
)