package com.example.sosalert.domain

sealed class SosEvents {
    data object OnStartSos : SosEvents()
}