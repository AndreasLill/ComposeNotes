package com.andlill.keynotes.app

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.utils.TimeUtils.daysBetween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Responsible for scheduled background maintenance such as deleting notes scheduled for deletion.
// Run once every 6 hours.
class MaintenanceJobService : JobService() {

    companion object {
        const val SERVICE_ID: Int = 100
        const val SERVICE_INTERVAL: Long = 6*60*60*1000
    }

    override fun onStartJob(params: JobParameters): Boolean {
        deleteScheduledNotes()
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    // Permanently delete notes in trash.
    private fun deleteScheduledNotes() = CoroutineScope(Dispatchers.Default).launch {
        NoteRepository.getAllNotes(this@MaintenanceJobService).firstOrNull()?.filter { it.note.deletion != null }?.forEach { note ->
            note.note.deletion?.let {
                if (it.daysBetween() <= 0) {
                    NoteRepository.deleteNote(this@MaintenanceJobService, note.note)
                    Log.d("MaintenanceJobService", "Scheduled deletion of note: ${note.note.id}")
                }
            }
        }
    }
}