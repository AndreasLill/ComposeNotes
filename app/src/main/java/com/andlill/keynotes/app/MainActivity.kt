package com.andlill.keynotes.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.andlill.keynotes.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNotificationChannel()
        initJobScheduler()

        // Ignore status bar and navigation bar insets to show content behind status bar.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }

    private fun initNotificationChannel() {
        val channel = NotificationChannel(MainReceiver.CHANNEL_ID, MainReceiver.CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.description = MainReceiver.CHANNEL_ID
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun initJobScheduler() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(this, MaintenanceJobService::class.java)
        val builder = JobInfo.Builder(MaintenanceJobService.SERVICE_ID, componentName).setPersisted(true).setPeriodic(MaintenanceJobService.SERVICE_INTERVAL)
        jobScheduler.schedule(builder.build())
    }
}