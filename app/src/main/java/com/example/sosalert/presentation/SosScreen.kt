package com.example.sosalert.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sosalert.presentation.component.BottomSheetContent
import com.example.sosalert.presentation.component.SosButton
import com.example.sosalert.presentation.component.StopSosButton
import com.example.sosalert.services.LocationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(modifier: Modifier) {
    val context = LocalContext.current

    var isSosActive by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
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

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                showSheet = true
            }
        ) {
            Text(
                text = "Menu",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            BottomSheetContent(
                onManageContacts = {
                    showSheet = false
                    // navigate to contact list screen
                },
                onEditMessage = {
                    showSheet = false
                    // navigate to message edit screen
                }
            )
        }
    }
}