package com.andlill.composenotes.app

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.dialog.ConfirmDialog
import com.andlill.composenotes.ui.shared.dialog.InputDialog
import com.andlill.composenotes.ui.theme.AppTheme
import com.andlill.composenotes.utils.DialogUtils

class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
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
                    snackbarHost = {
                        SnackbarHost(
                            modifier = Modifier.navigationBarsPadding(),
                            hostState = appState.snackbarHostState
                        )
                    },
                    content = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Navigation(appState)
                            ConfirmDialog(
                                state = DialogUtils.confirmDialogState,
                                body = DialogUtils.confirmDialogBody,
                                annotation = DialogUtils.confirmDialogAnnotation,
                                annotationStyle = DialogUtils.confirmDialogAnnotationStyle,
                                onDismiss = {
                                    DialogUtils.closeConfirmDialog()
                                },
                                onConfirm = {
                                    DialogUtils.confirmDialogListener?.invoke()
                                    DialogUtils.closeConfirmDialog()
                                }
                            )
                            InputDialog(
                                state = DialogUtils.inputDialogState,
                                title = DialogUtils.inputDialogTitle,
                                placeholder = DialogUtils.inputDialogPlaceholder,
                                onDismiss = {
                                    DialogUtils.closeInputDialog()
                                },
                                onConfirm = { result ->
                                    DialogUtils.inputDialogListener?.invoke(result)
                                    DialogUtils.closeInputDialog()
                                }
                            )
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