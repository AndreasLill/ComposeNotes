package com.andlill.keynotes.app.note

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.andlill.keynotes.app.MainReceiver
import java.util.*

object NoteBroadcaster {

    fun setReminder(context: Context, calendar: Calendar, noteId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.putExtra("id", noteId)
            intent.setPackage("com.andlill.keynotes")
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarm.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent), intent)
        Log.d("NoteBroadcaster", "Broadcast '$noteId' Sent")
    }

    fun cancelReminder(context: Context, noteId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.setPackage("com.andlill.keynotes")
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarm.cancel(intent)
        Log.d("NoteBroadcaster", "Broadcast '$noteId' Cancelled")
    }
}