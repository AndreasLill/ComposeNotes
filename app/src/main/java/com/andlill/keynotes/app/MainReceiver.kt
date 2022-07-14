package com.andlill.keynotes.app

import android.app.AlarmManager
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

            // Restore intent for active reminder.
            val intent = Intent(context, MainReceiver::class.java).let { intent ->
                intent.action = ACTION_REMINDER
                intent.putExtra("id", note.note.id)
                intent.setPackage("com.andlill.keynotes")
                PendingIntent.getBroadcast(context, note.note.id, intent, PendingIntent.FLAG_IMMUTABLE)
            }

            // Set reminder.
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setAlarmClock(AlarmManager.AlarmClockInfo(note.note.reminder, intent), intent)

            Log.d("MainReceiver", "Broadcast '${note.note.id}' Restored")
        }
    }

    // Send reminder by notification and remove reminder from note.
    private fun sendReminder(context: Context, intent: Intent) = CoroutineScope(Dispatchers.Default).launch {
        val id = intent.getIntExtra("id", 0)

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