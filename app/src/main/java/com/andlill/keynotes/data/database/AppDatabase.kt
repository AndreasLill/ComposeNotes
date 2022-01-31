package com.andlill.keynotes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andlill.keynotes.model.Note

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract val noteDao: NoteDao

    companion object {

        @Volatile
        private lateinit var instance: AppDatabase

        fun get(context: Context): AppDatabase {
            synchronized(this) {
                if (!this::instance.isInitialized) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "database").fallbackToDestructiveMigration().build()
                }

                return instance
            }
        }
    }
}