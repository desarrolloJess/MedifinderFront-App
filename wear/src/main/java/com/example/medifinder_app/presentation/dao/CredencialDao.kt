package com.example.medifinder_app.presentation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medifinder_app.presentation.entity.Credencial

@Dao
interface CredencialDao {
    @Insert
    suspend fun insertCredencial(credencial: Credencial)

    @Query("DELETE FROM credencial")
    suspend fun deleteAll()

    @Query("SELECT * FROM credencial")
    suspend fun getCredenciales(): Credencial?

}

