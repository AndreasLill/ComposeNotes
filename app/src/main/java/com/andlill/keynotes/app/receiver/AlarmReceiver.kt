package com.andlill.keynotes.app.receiver

import android.app.AlarmManager
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
    private fun restoreReminders(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        CoroutineScope(Dispatchers.Default).launch {
            NoteRepository.getAllNotes(context).first().forEach { note ->
                // Skip notes with no active reminders.
                if (note.reminder == null)
                    return@forEach

                // Restore all active reminders.
                val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
                    intent.action = ACTION_REMINDER
                    intent.putExtra("id", note.id)
                    intent.putExtra("color", note.color)
                    intent.putExtra("title", note.title)
                    intent.putExtra("text", note.body)
                    intent.setPackage("com.andlill.keynotes")
                    PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_IMMUTABLE)
                }
                alarm.setAlarmClock(AlarmManager.AlarmClockInfo(note.reminder, intent), intent)
                Log.d("MainReceiver", "Broadcast '${note.id}' Restored")
            }
        }
    }

    // Send reminder by notification and remove reminder from note.
    private fun sendReminder(context: Context, intent: Intent) {
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

        // Remove reminder from note.
        CoroutineScope(Dispatchers.Default).launch {
            NoteRepository.getNote(context, id).first()?.let { note ->
                NoteRepository.insertNote(context, note.copy(reminder = null))
            }
        }

        Log.d("MainReceiver", "Reminder '$id' Notification Sent")
    }
}