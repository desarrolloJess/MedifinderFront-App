package com.example.medifinder_app.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medifinder_app.R
import com.example.medifinder_app.presentation.ui.EncabezadoComposable
import kotlinx.coroutines.delay

@Composable
fun AppointmentCanceledActivity(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(4000)
        navController.navigate("home") {
            popUpTo("home") { inclusive = true }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        EncabezadoComposable()
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.icono_confirmacion),
            contentDescription = "Icono Confirmacion",
            modifier = Modifier
                .size(50.dp)
                .padding(3.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Su cita ha sido cancelada",
            fontSize = 10.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}