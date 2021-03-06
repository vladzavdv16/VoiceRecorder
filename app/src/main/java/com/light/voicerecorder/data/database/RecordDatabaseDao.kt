package com.light.voicerecorder.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.light.voicerecorder.data.database.model.RecordingItem

@Dao
interface RecordDatabaseDao {

    @Insert
    fun insert(record: RecordingItem)

    @Update
    fun update(record: RecordingItem)

    @Query("SELECT * from recording_table WHERE id = :key")
    fun getRecords(key: Long): RecordingItem

    @Query("DELETE FROM recording_table")
    fun clearAll()

    @Query("DELETE FROM recording_table WHERE id = :key")
    fun removeRecord(key: Long)

    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getAllRecords(): LiveData<MutableList<RecordingItem>>

    @Query("SELECT COUNT(*) FROM recording_table")
    fun getCount(): LiveData<Int>
}