package com.example.medifinder_app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medifinder_app.presentation.models.CitaModel
import com.example.medifinder_app.presentation.ui.EncabezadoComposable
import com.example.medifinder_app.presentation.utils.formatearFechaLetraConDia
import com.example.medifinder_app.presentation.utils.formatearHora

@Composable
fun AppointmentDetailsActivity(cita: CitaModel, navController: NavController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EncabezadoComposable()
        Text(
            text = "Cita con el Dr. " + cita.nombreMedico + " " + cita.apellidoMedico,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Detalles",
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Día: " + formatearFechaLetraConDia(cita.fechaInicio),
            fontSize = 8.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Hora: " + formatearHora(cita.fechaInicio),
            fontSize = 8.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Dirección: ${cita.direccionMedico}",
            fontSize = 8.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        if( cita.estatus == "1" ) {
            Button(
                onClick = {
                    navController.navigate("appointmentConfirm/${cita.id}")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF14967F),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .padding(horizontal = 10.dp, vertical = 0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Confirmar Cita",
                    fontSize = 10.sp,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    navController.navigate("appointmentCancel/${cita.id}")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFd30000),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .padding(horizontal = 10.dp, vertical = 0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Cancelar Cita",
                    fontSize = 10.sp,
                )
            }
        }else if(cita.estatus == "2"){
            Text(
                text = "Cita confirmada",
                fontSize = 8.sp,
                color = Color.Black
            )
        } else if(cita.estatus == "0"){
            Text(
                text = "Falta confirmación médica",
                fontSize = 8.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = {
                navController.navigate("home")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF095d7e),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .padding(horizontal = 10.dp, vertical = 0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "Regresar",
                fontSize = 10.sp,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}
