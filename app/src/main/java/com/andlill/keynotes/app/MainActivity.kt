package com.andlill.keynotes.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.core.view.WindowCompat
import com.andlill.keynotes.app.receiver.AlarmReceiver
import com.andlill.keynotes.ui.theme.AppTheme

@ExperimentalLayoutApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, AlarmReceiver.CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                description = AlarmReceiver.CHANNEL_ID
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Ignore status bar and navigation bar insets to show content behind status bar.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                // Provide status bar and navigation bar insets.
                Navigation()
            }
        }
    }
}