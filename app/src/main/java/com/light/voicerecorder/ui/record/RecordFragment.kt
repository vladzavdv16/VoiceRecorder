package com.light.voicerecorder.ui.record

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.light.voicerecorder.ui.MainActivity
import com.light.voicerecorder.R
import com.light.voicerecorder.data.database.RecordDatabase
import com.light.voicerecorder.data.database.RecordDatabaseDao
import com.light.voicerecorder.data.service.RecordService
import com.light.voicerecorder.databinding.FragmentRecordBinding
import java.io.File


class RecordFragment : Fragment(R.layout.fragment_record) {

    private var binding: FragmentRecordBinding? = null
    private lateinit var viewModel: RecordViewModel
    private lateinit var mainActivity: MainActivity
    private var count: Int? = null
    private var database: RecordDatabaseDao? = null
    private val MY_PERMISSIONS_RECORD_AUDIO = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = view.let { FragmentRecordBinding.bind(it) }

        database = context?.let { RecordDatabase.getInstance(it).recordDatabaseDao }

        mainActivity = activity as MainActivity

        viewModel.elapsedTime.observe(viewLifecycleOwner) {
            binding?.time?.text = it
        }

        if (!mainActivity.isRunningService()) {
            viewModel.resetTimer()
        } else {
            binding?.btnStart?.setImageResource(R.drawable.ic_stop_36dp)
        }

        binding?.btnStart?.setOnClickListener {
            createChannel(getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name))
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO)
            } else {
                if (mainActivity.isRunningService()) {
                    onRecord(false)
                    viewModel.stopTimer()
                } else {
                    onRecord(true)
                    viewModel.startTimer()
                }
            }
        }

    }

    private fun onRecord(start: Boolean) {
        val intent = Intent(activity, RecordService::class.java)

        if (start) {
            binding?.btnStart?.setImageResource(R.drawable.ic_stop_36dp)
            Toast.makeText(activity, R.string.toast_recording_start, Toast.LENGTH_SHORT).show()

            val folder =
                File(activity?.getExternalFilesDir(null)?.absolutePath.toString() + "/VoiceRecorder")
            if (!folder.exists()) {
                folder.mkdir()
            }

            activity?.startService(intent)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding?.btnStart?.setImageResource(R.drawable.ic_mic_36dp)

            activity?.stopService(intent)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            MY_PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onRecord(true)
                    viewModel.startTimer()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_recording_permissions),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}





