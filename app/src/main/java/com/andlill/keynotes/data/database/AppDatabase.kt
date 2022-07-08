package com.andlill.keynotes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note

@Database(entities = [Note::class, Label::class], version = 6, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract val noteDao: NoteDao
    abstract val labelDao: LabelDao

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