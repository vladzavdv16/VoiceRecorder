package com.light.voicerecorder.ui.listRecord

import androidx.lifecycle.ViewModel
import com.light.voicerecorder.data.database.RecordDatabaseDao

class ListFragmentViewModel(
    val dataSource: RecordDatabaseDao,
) : ViewModel() {

    val database = dataSource
    val records = database.getAllRecords()
}