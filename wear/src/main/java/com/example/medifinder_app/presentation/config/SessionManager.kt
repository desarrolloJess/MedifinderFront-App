package com.example.medifinder_app.presentation.config

import android.content.Context
import android.content.SharedPreferences
import com.example.medifinder_app.presentation.models.UsuarioModel
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: UsuarioModel) {
        val editor = prefs.edit()
        val userJson = gson.toJson(user)
        editor.putString("user", userJson)
        editor.apply()
    }
    // Verifica si el usuario está autenticado
    fun getUser(): UsuarioModel? {
        val userJson = prefs.getString("user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, UsuarioModel::class.java)
        } else {
            null
        }
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove("user")
        editor.apply()
    }
}