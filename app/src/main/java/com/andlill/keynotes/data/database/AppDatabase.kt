package com.andlill.keynotes.data.database

import android.content.Context
import androidx.room.*
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.model.NoteLabelJoin

//autoMigrations = [ AutoMigration(from = 5, to = 6) ]
@Database(version = 12, entities = [Note::class, Label::class, NoteLabelJoin::class], exportSchema = true)
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