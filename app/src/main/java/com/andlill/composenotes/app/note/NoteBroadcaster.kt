package com.andlill.composenotes.app.note

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.andlill.composenotes.app.MainReceiver

object NoteBroadcaster {

    fun setReminder(context: Context, time: Long, noteId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainReceiver::class.java).let { intent ->
            intent.action = MainReceiver.ACTION_REMINDER
            intent.putExtra(MainReceiver.EXTRA_NOTE_ID, noteId)
            intent.setPackage("com.andlill.composenotes")
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarm.canScheduleExactAlarms()) {
                // Some devices using API 31 requires permission "SCHEDULE_EXACT_ALARM".
                alarm.setExactAndAllowWhileIdle(RTC_WAKEUP, time, intent)
            }
            else {
                // Use non-exact as fallback.
                alarm.setAndAllowWhileIdle(RTC_WAKEUP, time, intent)
            }
        }
        else {
            alarm.setExactAndAllowWhileIdle(RTC_WAKEUP, time, intent)
        }
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