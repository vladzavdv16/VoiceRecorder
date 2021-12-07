package com.light.voicerecorder.ui.listRecord.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.light.voicerecorder.data.database.model.RecordingItem
import com.light.voicerecorder.databinding.ListItemRecordBinding
import com.light.voicerecorder.domain.model.Record
import java.util.concurrent.TimeUnit

class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {

    private lateinit var inflater: LayoutInflater

    var data = listOf<RecordingItem>()
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
        val itemDuration = data[position].length
        val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MILLISECONDS.toSeconds(minutes)

        holder.binding.recordName.text = data[position].name
        holder.binding.recordLength.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun getItemCount(): Int = data.size
}