package com.light.voicerecorder.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.light.voicerecorder.MainActivity
import com.light.voicerecorder.R
import com.light.voicerecorder.database.RecordDatabase
import com.light.voicerecorder.database.RecordDatabaseDao
import com.light.voicerecorder.database.RecordingItem
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

class RecordService : Service() {

    private var fileName: String? = null
    private var filePatch: String? = null
    private var countRecors: Int? = null

    private var recorder: MediaRecorder? = null

    private var startingTimeMillis: Long? = null
    private var elapsedTimeMillis: Long? = null

    private var database: RecordDatabaseDao? = null

    private lateinit var recordingItem: RecordingItem

    private val job = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val CHANNEL_ID = "RecordService"

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        database = RecordDatabase.getInstance(applicationContext).recordDatabaseDao
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

        countRecors = intent?.extras?.get("COUNT") as Int

        return START_STICKY
    }

    private fun startRecording() {

        setFileNameAndPath()

        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder?.setOutputFile(filePatch)
        recorder?.setAudioChannels(1)
        recorder?.setAudioEncodingBitRate(192000)

        try {
            recorder?.prepare()
            recorder?.start()
            startingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())

        } catch (e: IOException) {
            Log.e("RecordService", "prepare failed")
        }
    }

    private fun createNotification(): Notification {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mic_36dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_recording))
                .setOngoing(true)
        builder.setContentIntent(
            PendingIntent.getActivities(
                this, 0, arrayOf(
                    Intent(applicationContext, MainActivity::class.java)
                ), 0
            )
        )
        return builder.build()
    }

    private fun setFileNameAndPath() {

        var count = 0
        var f: File
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())

        do {
            fileName = (getString(R.string.default_file_name)) + "_" + dateTime + count + ".mp4"
            filePatch = application.getExternalFilesDir(null)?.absolutePath
            filePatch += "/$fileName"

            count++

            f = File(filePatch)
        } while (f.exists() && !f.isDirectory)

    }

    private fun stopRecording() {

        recorder?.stop()
        elapsedTimeMillis = System.currentTimeMillis() - startingTimeMillis!!
        recorder?.release()
        Toast.makeText(this, R.string.toast_recording_finish, Toast.LENGTH_SHORT).show()

        recordingItem.name = fileName.toString()
        recordingItem.filePatch = filePatch.toString()
        recordingItem.length = elapsedTimeMillis as Long
        recordingItem.time = System.currentTimeMillis()

        recorder = null

        try {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database?.insert(recordingItem)
                }
            }
        } catch (e: Exception) {
            Log.e("Record Service", "exception", e)
        }
    }

    override fun onDestroy() {
        if (recorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }
}