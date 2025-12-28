package com.example.sosalert.contact.data

import androidx.compose.runtime.mutableStateListOf
import com.example.sosalert.contact.domain.EmergencyContact

object EmergencyContactStore {
    val contacts = mutableStateListOf<EmergencyContact>()
}