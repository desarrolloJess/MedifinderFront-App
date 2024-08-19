package com.example.medifinder_app.presentation.models

import com.google.gson.annotations.SerializedName
data class UsuarioModel(
    @SerializedName("id") val idPaciente: Int,
    @SerializedName("nombreCompleto") val nombreCompleto: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("sexo") val sexo: String,
    @SerializedName("estatus") val estatus: String,
    @SerializedName("contrasena") val contrasena: String
)