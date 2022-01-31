package com.andlill.keynotes.app.note

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
object NoteBroadcaster {

    fun setAlarm(context: Context, calendar: Calendar, id: Int, text: String) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.andlill.keynotes.MainReceiver").apply {
            putExtra("id", id)
            putExtra("contentText", text)
            setPackage("com.andlill.keynotes")
        }
        val intentPending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, intentPending)
        Log.d("NoteBroadcaster", "Broadcast '$id' Sent")
    }

    fun cancelAlarm(context: Context, id: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.andlill.keynotes.MainReceiver").apply {
            setPackage("com.andlill.keynotes")
        }
        val intentPending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Must cancel both alarm and intent.
        alarm.cancel(intentPending)
        intentPending.cancel()
        Log.d("NoteBroadcaster", "Broadcast '$id' Cancelled")
    }
}