package com.example.medifinder_app.presentation.models

import com.google.gson.annotations.SerializedName
data class CitaModel(
    @SerializedName("id") val id: Int,
    @SerializedName("idMedico") val idMedico: Int,
    @SerializedName("fechaInicio") val fechaInicio: String,
    @SerializedName("fechaFin") val fechaFin: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("estatus") val estatus: String,
    @SerializedName("fechaCancelacion") val fechaCancelacion: String?,
    @SerializedName("motivoCancelacion") val motivoCancelacion: String?,
    @SerializedName("nombreMedico") val nombreMedico: String,
    @SerializedName("apellidoMedico") val apellidoMedico: String,
    @SerializedName("direccionMedico") val direccionMedico: String
)