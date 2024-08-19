package com.example.medifinder_app.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme

@Composable
fun ButtonComposable (color: Color, text: String, onClick: () -> Unit) {
    MaterialTheme {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .padding(horizontal = 10.dp, vertical = 0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = text,
                fontSize = 10.sp,
            )
        }
    }
}