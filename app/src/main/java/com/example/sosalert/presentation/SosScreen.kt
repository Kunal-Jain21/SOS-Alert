package com.example.sosalert.presentation

import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sosalert.services.LocationService
import com.example.sosalert.ui.theme.White
import com.example.sosalert.utils.hasPermission

@Composable
fun SosScreen() {
    val context = LocalContext.current

    var isSosActive by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSosActive) {
            StopSosButton(
                onStop = {
                    val intent = Intent(context, LocationService::class.java)
                    context.stopService(intent)
                    isSosActive = false
                }
            )
        } else {
            SosButton(
                onAllPermissionsGranted = {
                    val intent = Intent(context, LocationService::class.java)
                    ContextCompat.startForegroundService(context, intent)
                    isSosActive = true
                }
            )
        }
    }
}

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

@Composable
fun SosButton(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.all { it.value }
        if (allGranted) {
            onAllPermissionsGranted()
        } else {
            Toast.makeText(
                context,
                "Allow all Permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handleSosClick() {
        val allGranted = requiredPermissions.all {
            context.hasPermission(it)
        }

        if (allGranted) {
            onAllPermissionsGranted()
            return
        }

        permissionLauncher.launch(
            requiredPermissions
        )
    }

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
                onClick = { handleSosClick() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SOS",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )

            Text(
                text = "PRESS & HOLD",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
            )
        }
    }
}