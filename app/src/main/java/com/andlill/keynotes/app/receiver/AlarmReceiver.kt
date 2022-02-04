package com.andlill.keynotes.app.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.andlill.keynotes.R
import com.andlill.keynotes.app.MainActivity
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "com.andlill.keynotes.AlarmReceiverChannel"
        const val ACTION_REMINDER = "com.andlill.keynotes.Reminder"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MainReceiver", "Broadcast '${intent.action}' Received")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // TODO: Restart reminders at boot action.
                restartReminders(context, intent)
            }
            ACTION_REMINDER -> {
                reminder(context, intent)
            }
        }
    }

    private fun restartReminders(context: Context, intent: Intent) {

    }

    private fun reminder(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 0)
        sendNotification(context, intent)

        // Reset note to 0 (if not repeating reminder)
        CoroutineScope(Dispatchers.Default).launch {
            NoteRepository.getNote(context, id).first()?.let { note ->
                NoteRepository.insertNote(context, note.copy(reminder = null))
            }
        }
    }

    private fun sendNotification(context: Context, intent: Intent) {
        val contentTitle = intent.getStringExtra("title")
        val contentText = intent.getStringExtra("text")
        val id = intent.getIntExtra("id", 0)
        val color = intent.getIntExtra("color", Color.WHITE)

        // Open note on notification click.
        val resultIntent = Intent(
            Intent.ACTION_VIEW,
            String.format(Screen.NoteScreen.uri[0], id).toUri(),
            context,
            MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(Random.nextInt(), PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setColor(color)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }
}