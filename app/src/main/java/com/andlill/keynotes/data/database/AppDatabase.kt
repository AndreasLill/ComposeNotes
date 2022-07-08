package com.andlill.keynotes.data.database

import android.content.Context
import androidx.room.*
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note

//autoMigrations = [ AutoMigration(from = 5, to = 6) ]
@Database(version = 6, entities = [Note::class, Label::class], exportSchema = true)
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