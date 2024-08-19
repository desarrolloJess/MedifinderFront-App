package com.example.medifinder_app.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import coil.compose.rememberImagePainter
import com.example.medifinder_app.R

val BackgroundColor = Color(0xFF14967F)
val CircleColor = Color.White
@Composable
fun EncabezadoComposable() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(BackgroundColor)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp_sinnombre),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .offset(y = (15).dp)
                        .clip(CircleShape)
                        .background(CircleColor)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}