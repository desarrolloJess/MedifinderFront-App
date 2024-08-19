package com.example.medifinder_app.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medifinder_app.presentation.apiservice.RetrofitClient
import com.example.medifinder_app.presentation.models.CitaModel
import com.example.medifinder_app.presentation.ui.ButtonComposable
import com.example.medifinder_app.presentation.ui.EncabezadoComposable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AppointmentConfirmActivity(cita: CitaModel, navController: NavController) {
    val scrollState = rememberScrollState()
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EncabezadoComposable()
        Text(
            text = "¿Desea realizar la confirmación de su cita con el Dr. ${cita.nombreMedico} ${cita.apellidoMedico}?",
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "En cualquier otro momento usted podrá reagendar o cancelar su cita aún confirmando su cita en este momento",
            fontSize = 8.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(5.dp))
        ButtonComposable(
            Color(0xFF14967F),
            "Si, Confirmar Cita",
            onClick = {
                Log.i("Daniel", "Botón si, confirmar Cita presionado")
                confirmarCita(cita.id, navController)
            })
        Spacer(modifier = Modifier.height(5.dp))
        ButtonComposable(Color(
            0xFF095d7e),
            "No, aún no quiero confirmar",
            onClick = {
                Log.i("Daniel", "Botón no, aun no Cita presionado")
                navController.navigate("home")
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}

private fun confirmarCita(id: Int, navController: NavController) {
    RetrofitClient.instance.confirmarCita(id).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                Log.i("Daniel", "Cita confirmada con éxito")
                navController.navigate("appointmentConfirmed")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e("Daniel", "Error al confirmar cita: $errorBody")
                navController.navigate("appointmentConfirmedError")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("Daniel", "Fallo en la llamada a la API: ${t.message}")
            navController.navigate("appointmentConfirmedError")
        }
    })
}