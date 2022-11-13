package com.andlill.composenotes.data.database

import android.content.Context
import androidx.room.*
import com.andlill.composenotes.model.*

@Database(
    version = 2,
    entities = [Note::class, NoteCheckBox::class, Label::class, NoteLabelJoin::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){

    abstract val noteDao: NoteDao
    abstract val labelDao: LabelDao

    companion object {

        @Volatile
        private lateinit var instance: AppDatabase

        fun get(context: Context): AppDatabase {
            synchronized(this) {
                if (!this::instance.isInitialized) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "database").fallbackToDestructiveMigrationOnDowngrade().build()
                }

                return instance
            }
        }
    }
}