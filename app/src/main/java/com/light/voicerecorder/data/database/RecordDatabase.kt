package com.light.voicerecorder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.light.voicerecorder.data.database.model.RecordingItem

@Database(entities = [RecordingItem::class], version = 1, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {

    abstract val recordDatabaseDao: RecordDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: RecordDatabase? = null

        fun getInstance(context: Context): RecordDatabase {
            synchronized(this) {
                var instance: RecordDatabase? = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordDatabase::class.java,
                        "record_app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}