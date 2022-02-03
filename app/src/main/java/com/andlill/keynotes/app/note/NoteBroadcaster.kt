package com.andlill.keynotes.app.note

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.andlill.keynotes.app.MainReceiver
import com.andlill.keynotes.model.Note
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
object NoteBroadcaster {

    fun setReminder(context: Context, calendar: Calendar, note: Note) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.putExtra("id", note.id)
            intent.putExtra("color", note.color)
            intent.putExtra("title", note.title)
            intent.putExtra("text", note.body)
            intent.setPackage("com.andlill.keynotes")
            PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarm.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent), intent)
        Log.d("NoteBroadcaster", "Broadcast '${note.id}' Sent")
    }

    fun cancelReminder(context: Context, note: Note) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.setPackage("com.andlill.keynotes")
            PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarm.cancel(intent)
        Log.d("NoteBroadcaster", "Broadcast '${note.id}' Cancelled")
    }
}