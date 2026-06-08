package com.egekilavuz.economylab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.egekilavuz.economylab.data.NotificationHelper
import com.egekilavuz.economylab.ui.theme.EconomyLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification setup
        NotificationHelper.createChannel(this)
        NotificationHelper.scheduleDailyAlarm(this)

        enableEdgeToEdge()
        setContent {
            EconomyLabTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
