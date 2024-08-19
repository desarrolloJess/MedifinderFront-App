package com.example.medifinder_app.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatearFecha(dateString: String): String {
    return try {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = originalFormat.parse(dateString)
        date?.let { targetFormat.format(it) } ?: "Fecha no válida"
    } catch (e: Exception) {
        "Error al formatear la fecha"
    }
}

fun formatearFechaLetra(dateString: String): String {
    return try {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val targetFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
        val date = originalFormat.parse(dateString)

        date?.let {
            val calendar = Calendar.getInstance().apply { time = it }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = targetFormat.format(it).split(" ")[1] // Obtiene el nombre del mes
            val year = calendar.get(Calendar.YEAR)
            "$day de $month del $year"
        } ?: "Fecha no válida"
    } catch (e: Exception) {
        "Error al formatear la fecha"
    }
}


fun formatearFechaLetraConDia(dateString: String): String {
    return try {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        val date = originalFormat.parse(dateString)

        date?.let {
            targetFormat.format(it)
        } ?: "Fecha no válida"
    } catch (e: Exception) {
        "Error al formatear la fecha " + e.message
    }
}

fun formatearHora(dateString: String): String {
    return try {
        // Define el formato de entrada
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        // Define el formato de salida con AM/PM
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        // Parsear la fecha
        val date = originalFormat.parse(dateString)

        date?.let {
            timeFormat.format(it)
        } ?: "Hora no válida"
    } catch (e: Exception) {
        "Error al formatear la hora " +e.message
    }
}