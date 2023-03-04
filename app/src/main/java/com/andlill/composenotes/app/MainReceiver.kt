package com.andlill.composenotes.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.andlill.composenotes.R
import com.andlill.composenotes.app.note.NoteBroadcaster
import com.andlill.composenotes.data.repository.NoteRepository
import com.andlill.composenotes.model.NoteReminderRepeat
import com.andlill.composenotes.utils.TimeUtils.toLocalDateTime
import com.andlill.composenotes.utils.TimeUtils.toMilliSeconds
import com.andlill.composenotes.utils.TimeUtils.withClosestDayOfMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "com.andlill.composenotes.AlarmReceiverChannel"
        const val ACTION_REMINDER = "com.andlill.composenotes.Reminder"
        const val EXTRA_NOTE_ID = "com.andlill.composenotes.extra.noteId"
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
                Screen.NoteScreen.deepLink(noteId = id).toUri(),
                context,
                MainActivity::class.java)
            val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(resultIntent)
                getPendingIntent(Random.nextInt(), PendingIntent.FLAG_IMMUTABLE)
            }

            var body = context.resources.getString(R.string.notification_reminder_empty)
            if (note.checkBoxes.isNotEmpty()) {
                var bodyStr = ""
                note.checkBoxes.forEach { checkBox ->
                    if (checkBox.text.isBlank())
                        return@forEach
                    bodyStr += checkBox.text.plus(System.lineSeparator())
                }
                body = bodyStr
            }
            if (note.note.body.isNotBlank()) {
                body = note.note.body
            }

            // Build the notification.
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active)
                .setContentTitle(note.note.title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(note.note.body))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build()

            // Send notification.
            with(NotificationManagerCompat.from(context)) {
                // TODO: Handle permission events for notifications.
                notify(id, notification)
            }

            // Remove non-repeating reminder or update repeating reminder.
            val reminderTime = when (note.note.reminderRepeat) {
                NoteReminderRepeat.REPEAT_DAILY -> {
                    note.note.reminder?.toLocalDateTime()?.plusDays(1)?.toMilliSeconds()
                }
                NoteReminderRepeat.REPEAT_WEEKLY -> {
                    note.note.reminder?.toLocalDateTime()?.plusWeeks(1)?.toMilliSeconds()
                }
                NoteReminderRepeat.REPEAT_MONTHLY -> {
                    // Get the day of the month in the reminder date selection made originally by the user.
                    val dayOfMonth = note.note.reminderSelection?.toLocalDateTime()?.dayOfMonth!!
                    // Get the next month with the closest day of the month available.
                    // This is to handle repeating end of the months reminders with different month lengths.
                    note.note.reminder?.toLocalDateTime()?.plusMonths(1)?.withClosestDayOfMonth(dayOfMonth)?.toMilliSeconds()
                }
                NoteReminderRepeat.REPEAT_YEARLY -> {
                    note.note.reminder?.toLocalDateTime()?.plusYears(1)?.toMilliSeconds()
                }
                else -> null
            }

            NoteRepository.setNoteReminder(context, note.note.id, reminderTime)
        }
    }
}