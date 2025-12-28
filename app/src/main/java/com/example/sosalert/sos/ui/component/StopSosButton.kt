package com.example.sosalert.sos.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sosalert.ui.theme.White

@Composable
fun StopSosButton(
    onStop: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(250.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE53535),
                        Color(0xFFD32F2F)
                    ),
                    radius = 400f
                )
            )
            .clickable(
                onClick = onStop
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "STOP",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        )
    }
}