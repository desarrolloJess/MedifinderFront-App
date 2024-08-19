package com.example.medifinder_app.presentation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credencial")
data class Credencial(
    @PrimaryKey val email: String,
    val password: String
)
