package com.andlill.keynotes.app

import android.annotation.SuppressLint
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
import com.andlill.keynotes.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

@SuppressLint("UnspecifiedImmutableFlag")
class MainReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val contentTitle = intent.getStringExtra("title")
        val contentText = intent.getStringExtra("text")
        val id = intent.getIntExtra("id", 0)
        val color = intent.getIntExtra("color", Color.WHITE)

        // Open app on notification click.
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(Random.nextInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, "APP_CHANNEL_ID")
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

        // Cancel both alarm and intent pending after consuming the intent.
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentPending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarm.cancel(intentPending)
        intentPending.cancel()

        // Reset note to 0 (if not repeating reminder)
        CoroutineScope(Dispatchers.Default).launch {
            NoteRepository.getNote(context, id).first()?.let { note ->
                NoteRepository.insertNote(context, note.copy(reminder = 0))
            }
        }

        Log.d("MainReceiver", "Broadcast '$id' Received")
    }
}