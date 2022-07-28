package com.andlill.keynotes.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.andlill.keynotes.R
import com.andlill.keynotes.app.note.NoteBroadcaster
import com.andlill.keynotes.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "com.andlill.keynotes.AlarmReceiverChannel"
        const val ACTION_REMINDER = "com.andlill.keynotes.Reminder"
        const val EXTRA_NOTE_ID = "com.andlill.keynotes.extra.noteId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                restoreReminders(context)
            }
            ACTION_REMINDER -> {
                sendReminder(context, intent)
            }
        }
    }

    // Restore any reminders lost by system reboot.
    private fun restoreReminders(context: Context) = CoroutineScope(Dispatchers.Default).launch {
        NoteRepository.getAllNotes(context).firstOrNull()?.forEach { note ->

            // Skip notes with no active reminders.
            if (note.note.reminder == null)
                return@forEach

            NoteBroadcaster.setReminder(context, note.note.reminder, note.note.id)
        }
    }

    // Send reminder by notification and remove reminder from note.
    private fun sendReminder(context: Context, intent: Intent) = CoroutineScope(Dispatchers.Default).launch {
        val id = intent.getIntExtra(EXTRA_NOTE_ID, -1)

        NoteRepository.getNote(context, id).firstOrNull()?.let { note ->

            // Create intent to open note on notification click.
            val resultIntent = Intent(
                Intent.ACTION_VIEW,
                String.format(Screen.NoteScreen.uri[0], id).toUri(),
                context,
                MainActivity::class.java)
            val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(resultIntent)
                getPendingIntent(Random.nextInt(), PendingIntent.FLAG_IMMUTABLE)
            }

            // Build the notification.
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active)
                .setContentTitle(note.note.title)
                .setContentText(note.note.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(note.note.body))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build()

            // Send notification.
            with(NotificationManagerCompat.from(context)) {
                notify(id, notification)
            }

            // Remove reminder from note after consuming alarm.
            NoteRepository.updateNote(context, note.note.copy(reminder = null))
        }

        Log.d("MainReceiver", "Reminder '$id' Notification Sent")
    }
}