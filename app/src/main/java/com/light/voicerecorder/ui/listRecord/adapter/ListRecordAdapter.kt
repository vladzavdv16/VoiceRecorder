package com.light.voicerecorder.ui.listRecord.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.light.voicerecorder.data.database.model.RecordingItem
import com.light.voicerecorder.databinding.ListItemRecordBinding
import com.light.voicerecorder.ui.dialog.RemoveDialogFragment
import com.light.voicerecorder.ui.player.PlayerFragment
import java.io.File
import java.util.concurrent.TimeUnit

class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {

    private lateinit var inflater: LayoutInflater

    var data = listOf<RecordingItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(val binding: ListItemRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        inflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemRecordBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val recordingItem = data[position]
        val itemDuration = data[position].length
        val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MILLISECONDS.toSeconds(minutes)

        holder.binding.recordName.text = data[position].name
        holder.binding.recordLength.text = String.format("%02d:%02d", minutes, seconds)

        holder.binding.delete.setOnClickListener {
           removeItemDialog(recordingItem, context)
        }

        holder.binding.cardView.setOnClickListener {
            val filePath = recordingItem.filePath

            val file = File(filePath)
            if (file.exists()) {
                try {
                    playRecord(filePath, context)
                } catch (e: Exception) { }
            } else {
                Toast.makeText(context, "Аудиофайл не найден", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int = data.size

    private fun playRecord(filePath: String, context: Context){
        val playerFragment = PlayerFragment().newInstance(filePath)
        val fragmentTransaction = (context as FragmentActivity)
            .supportFragmentManager
            .beginTransaction()
        playerFragment.show(fragmentTransaction, "dialog_playback")
    }

    private fun removeItemDialog(
        recordingItem: RecordingItem,
        context: Context?
    ) {
        val removeDialogFragment: RemoveDialogFragment =
            RemoveDialogFragment()
                .newInstance(
                    recordingItem.id,
                    recordingItem.filePath)
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        removeDialogFragment.show(transaction, "dialog_remove")
    }

}