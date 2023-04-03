package com.andlill.composenotes.app.note

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andlill.composenotes.app.MainReceiver

object NoteBroadcaster {

    fun setReminder(context: Context, noteId: Int, time: Long) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.putExtra(MainReceiver.EXTRA_NOTE_ID, noteId)
            intent.setPackage("com.andlill.composenotes")
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarm.setExactAndAllowWhileIdle(RTC_WAKEUP, time, intent)
    }

    fun cancelReminder(context: Context, noteId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.setPackage("com.andlill.composenotes")
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarm.cancel(intent)
    }
}