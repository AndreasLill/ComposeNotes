package com.andlill.keynotes.app.note

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.andlill.keynotes.model.Note
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
object NoteBroadcaster {

    fun setAlarm(context: Context, calendar: Calendar, note: Note) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.andlill.keynotes.MainReceiver").apply {
            putExtra("id", note.id)
            putExtra("color", note.color)
            putExtra("title", note.title)
            putExtra("text", note.body)
            setPackage("com.andlill.keynotes")
        }
        val intentPending = PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, intentPending)
        Log.d("NoteBroadcaster", "Broadcast '${note.id}' Sent")
    }

    fun cancelAlarm(context: Context, note: Note) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.andlill.keynotes.MainReceiver").apply {
            setPackage("com.andlill.keynotes")
        }
        val intentPending = PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Must cancel both alarm and intent.
        alarm.cancel(intentPending)
        intentPending.cancel()
        Log.d("NoteBroadcaster", "Broadcast '${note.id}' Cancelled")
    }
}