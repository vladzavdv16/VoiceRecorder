package com.light.voicerecorder.ui.record

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class RecordViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TRIGGER_TIME = "TRIGGER_AT"
    private val second = 1_000L

    private val prefs =
        app.getSharedPreferences("com.light.voicerecorder",
            Context.MODE_PRIVATE)


}