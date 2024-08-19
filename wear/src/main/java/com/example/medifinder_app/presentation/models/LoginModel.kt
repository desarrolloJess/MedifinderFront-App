package com.example.medifinder_app.presentation.models

import com.google.gson.annotations.SerializedName
data class LoginModel(
    //@SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("contrasena") val contrasena: String,
)