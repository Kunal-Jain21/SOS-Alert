package com.example.sosalert.sos.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.content.ContextCompat
import com.example.sosalert.contact.data.EmergencyContactStore
import com.example.sosalert.sos.services.LocationService
import com.example.sosalert.sos.ui.component.BottomSheetContent
import com.example.sosalert.sos.ui.component.SosButton
import com.example.sosalert.sos.ui.component.StopSosButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(
    onContact: () -> Unit,
    onMessage: () -> Unit
) {
    val context = LocalContext.current
    var isSosActive by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showMenuSheet by remember { mutableStateOf(false) }
    var showNoContactsDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showMenuSheet = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                        if (EmergencyContactStore.contacts.isEmpty()) {
                            showNoContactsDialog = true
                        } else {
                            val intent = Intent(context, LocationService::class.java)
                            ContextCompat.startForegroundService(context, intent)
                            isSosActive = true
                        }
                    }
                )
            }
        }

        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = sheetState
            ) {
                BottomSheetContent(
                    onManageContacts = {
                        showMenuSheet = false
                        onContact()
                    },
                    onEditMessage = {
                        showMenuSheet = false
                        onMessage()
                    }
                )
            }
        }

        if (showNoContactsDialog) {
            AlertDialog(
                onDismissRequest = { showNoContactsDialog = false },
                title = { Text("No Emergency Contacts") },
                text = {
                    Text("Please add at least one emergency contact before starting SOS.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showNoContactsDialog = false
                            onContact()
                        }
                    ) {
                        Text("Add Contacts")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoContactsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}