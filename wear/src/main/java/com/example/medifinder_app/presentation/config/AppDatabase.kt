package com.example.medifinder_app.presentation.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medifinder_app.presentation.dao.CredencialDao
import com.example.medifinder_app.presentation.entity.Credencial

@Database(entities = [Credencial::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun credencialDao(): CredencialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
