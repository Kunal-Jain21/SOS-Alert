package com.example.sosalert.contact.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.sosalert.contact.domain.EmergencyContact

@Composable
fun ContactRow(
    contact: EmergencyContact,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(contact.name) },
        supportingContent = { Text(contact.number) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    )
}