package com.andlill.keynotes.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.theme.AppTheme
import com.google.accompanist.insets.ProvideWindowInsets

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ignore status bar and navigation bar insets to show content behind status bar.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                // Register notification channel for API 26+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(MainReceiver.CHANNEL_ID, MainReceiver.CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH).apply {
                        description = MainReceiver.CHANNEL_ID
                    }
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }

                // Provide status bar and navigation bar insets.
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Navigation()
                }
            }
        }
    }
}