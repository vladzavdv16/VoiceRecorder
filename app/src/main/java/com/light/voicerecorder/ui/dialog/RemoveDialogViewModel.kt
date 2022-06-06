package com.light.voicerecorder.ui.dialog

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.light.voicerecorder.R
import com.light.voicerecorder.data.database.RecordDatabase
import com.light.voicerecorder.data.database.RecordDatabaseDao
import kotlinx.coroutines.*
import java.io.File
import java.lang.IllegalArgumentException

class RemoveDialogViewModel(
	private var databaseDao: RecordDatabaseDao,
	private val application: Application
): ViewModel() {

	private val job = Job()
	private val uiScope = CoroutineScope(Dispatchers.Main + job)

	fun removeItem(itemId: Long) {
		databaseDao = RecordDatabase.getInstance(application).recordDatabaseDao

		try {
			uiScope.launch {
				withContext(Dispatchers.IO) {
					databaseDao.removeRecord(itemId)
				}
			}
		} catch (e: Exception) {
			Log.e("removeItem", "exception", e)
		}
	}

	fun removeFile(path: String){
		val file = File(path)
		if (file.exists()){
			file.delete()
			Toast.makeText(application, R.string.file_deleted_text, Toast.LENGTH_SHORT).show()
		}
	}
}

class RemoveDialogViewModelFactory(
	private val databaseDao: RecordDatabaseDao,
	private var application: Application
): ViewModelProvider.Factory{
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(RemoveDialogViewModel::class.java)) {
			return RemoveDialogViewModel(databaseDao, application) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}