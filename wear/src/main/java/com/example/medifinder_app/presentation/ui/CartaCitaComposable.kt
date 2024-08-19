package com.example.medifinder_app.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.medifinder_app.R
import com.example.medifinder_app.presentation.models.CitaModel
import com.example.medifinder_app.presentation.utils.formatearFechaLetraConDia
import com.example.medifinder_app.presentation.utils.formatearHora


@Composable
fun CartaCitaComposable(cita: CitaModel, navController: NavController) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .background(Color(0xFFDCDCDC))
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    navController.navigate("appointmentDetails/${cita.id}")
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.imagen_icono),
                    contentDescription = "Icono calendario",
                    modifier = Modifier
                        .weight(2f)
                        .padding(3.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                )
                Spacer(modifier = Modifier.width(5.dp))
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = "Cita con ${cita.nombreMedico} ${cita.apellidoMedico}",
                        fontSize = 9.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = formatearFechaLetraConDia(cita.fechaInicio),
                        fontSize = 7.sp,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = formatearHora(cita.fechaInicio),
                        fontSize = 7.sp,
                        color = Color.Black
                    )
                    Text(
                        text = if (cita.estatus == "0") "Pendiente confirm. médica" else if (cita.estatus == "1") "Pendiente por confirmar" else "Confirmada",
                        fontSize = 7.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
    Log.e("DANIEL", formatearFechaLetraConDia(cita.fechaInicio))
    Log.e("DANIEL", formatearHora(cita.fechaInicio))
}