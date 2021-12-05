package com.light.voicerecorder.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recording_table")
data class RecordingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "time")
    var time: Long,
    @ColumnInfo(name = "length")
    var length: Long,
    @ColumnInfo(name = "filePatch")
    var filePatch: String
) {
}