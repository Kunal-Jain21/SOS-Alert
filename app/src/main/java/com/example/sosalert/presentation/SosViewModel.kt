package com.example.sosalert.presentation

import androidx.lifecycle.ViewModel
import com.example.sosalert.domain.SosEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SosViewModel: ViewModel() {

    private val _sosEvents = MutableSharedFlow<SosEvents>()
    val sosEvents = _sosEvents.asSharedFlow()




}