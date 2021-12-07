package com.light.voicerecorder.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.light.voicerecorder.data.database.RecordDatabaseDao
import com.light.voicerecorder.ui.listRecord.ListFragmentViewModel

class ListRecordViewModelFactory(private val databaseDao: RecordDatabaseDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListFragmentViewModel::class.java)) {
            return ListFragmentViewModel(dataSource = databaseDao) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}