package com.ilynehdev.leaflet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ilynehdev.core.designsystem.LeafletTheme

class LeafletActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafletTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
