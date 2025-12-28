package com.example.sosalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.sosalert.navigation.SosAppNavigation
import com.example.sosalert.ui.theme.SOSAlertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SOSAlertTheme {
                Scaffold {
                    SosAppNavigation(
                        modifier = Modifier.padding(it),
                    )
                }
            }
        }
    }
}