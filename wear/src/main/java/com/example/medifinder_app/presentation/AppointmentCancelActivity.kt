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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medifinder_app.presentation.apiservice.RetrofitClient
import com.example.medifinder_app.presentation.models.CitaModel
import com.example.medifinder_app.presentation.requests.CancelacionRequest
import com.example.medifinder_app.presentation.ui.ButtonComposable
import com.example.medifinder_app.presentation.ui.EncabezadoComposable
import com.example.medifinder_app.presentation.ui.RadioButtonsComposable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AppointmentCancelActivity(cita: CitaModel, navController: NavController) {
    var selectedOption by remember { mutableStateOf("Cambio planes personales") }
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
            text = "¿Hay algún motivo por el cual quiere cancelar su cita?",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(5.dp))


        RadioButtonsComposable(
            options = listOf(
                "Cambio planes personales",
                "Compromisos laborales",
                "Problemas financieros",
                "Mejora de síntomas",
                "Reprogramación con otro médico",
                "Otro"
            ),
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it }
        )

        Spacer(modifier = Modifier.height(5.dp))
        ButtonComposable(
            Color(0xFF14967F),
            "Cancelar cita",
            onClick = {
                Log.i("Daniel", "Botón si, confirmar Cita presionado")
                //navController.navigate("appointmentCanceled")
                cancelarCita(cita.id, selectedOption, navController)
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        ButtonComposable(Color(
            0xFF095d7e),
            "Regresar",
            onClick = {
                Log.i("Daniel", "Botón no, aun no Cita presionado")
                navController.navigate("home")
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}

private fun cancelarCita(id: Int, motivo: String, navController: NavController) {
    val cancelacionRequest = CancelacionRequest(motivo)
    RetrofitClient.instance.cancelarCita(id, cancelacionRequest).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                // Manejar la respuesta exitosa
                Log.i("DANIEL", "Cita cancelada exitosamente")
                navController.navigate("appointmentCanceled")
            } else {
                // Manejar la respuesta de error
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e("DANIEL", "Error: $errorBody")
                navController.navigate("appointmentCanceledError")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            // Manejar el fallo de la solicitud
            Log.e("DANIEL", "Failure: ${t.message}")
            navController.navigate("appointmentCanceledError")
        }
    })
}

