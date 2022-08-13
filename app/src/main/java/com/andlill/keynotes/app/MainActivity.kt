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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.AppSnackbar
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
                val appState = rememberAppState()
                Scaffold(
                    scaffoldState = appState.scaffold,
                    snackbarHost = { appState.scaffold.snackbarHostState },
                    content = { innerPadding ->
                        Box(modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()) {
                            Navigation(appState)
                            AppSnackbar(state = appState.scaffold.snackbarHostState, modifier = Modifier
                                .navigationBarsPadding()
                                .align(Alignment.BottomCenter))
                        }
                    }
                )
            }
        }
    }

    private fun initNotificationChannel() {
        val channel = NotificationChannel(MainReceiver.NOTIFICATION_CHANNEL_ID, resources.getString(R.string.notification_reminder_name), NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.description = resources.getString(R.string.notification_reminder_description)
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