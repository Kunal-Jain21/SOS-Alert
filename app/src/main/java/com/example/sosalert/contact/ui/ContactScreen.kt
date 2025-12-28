package com.example.sosalert.contact.ui

import android.Manifest
import android.app.Activity
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.sosalert.contact.ui.component.ContactRow
import com.example.sosalert.contact.data.EmergencyContactStore
import com.example.sosalert.contact.domain.EmergencyContact
import com.example.sosalert.utils.hasPermission
import com.example.sosalert.utils.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val contacts = EmergencyContactStore.contacts

    var showPermissionRationale by remember { mutableStateOf(false) }

    // 🔹 Contact Picker
    val contactPickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickContact()
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult

            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(uri.lastPathSegment),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val name =
                        it.getString(
                            it.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            )
                        )
                    val number =
                        it.getString(
                            it.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )

                    if (contacts.none { c -> c.number == number }) {
                        contacts.add(EmergencyContact(name, number))
                    }
                }
            }
        }

    // 🔹 Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                contactPickerLauncher.launch(null)
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val granted = context.hasPermission(Manifest.permission.READ_CONTACTS)

                    if (granted) {
                        contactPickerLauncher.launch(null)
                    } else if (
                        activity.shouldShowRationale(Manifest.permission.READ_CONTACTS)
                    ) {
                        showPermissionRationale = true
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No emergency contacts added")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(
                    items = contacts,
                    key = { it.number }
                ) { contact ->
                    ContactRow(
                        contact = contact,
                        onDelete = {
                            contacts.remove(contact)
                        }
                    )
                }
            }
        }
    }

    // 🔸 Rationale Dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permission Required") },
            text = {
                Text("Contacts permission is needed to select emergency contacts.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                ) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

