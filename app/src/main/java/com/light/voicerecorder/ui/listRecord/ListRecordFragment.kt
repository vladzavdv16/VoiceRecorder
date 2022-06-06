package com.light.voicerecorder.ui.listRecord

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.light.voicerecorder.data.database.RecordDatabase
import com.light.voicerecorder.databinding.FragmentListRecordBinding
import com.light.voicerecorder.ui.listRecord.adapter.ListRecordAdapter
import com.light.voicerecorder.utils.ListRecordViewModelFactory


class ListRecordFragment : Fragment() {

    private lateinit var viewModel: ListFragmentViewModel
    private var binding: FragmentListRecordBinding? = null
    private lateinit var adapter: ListRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ListRecordAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): FrameLayout? {
        // Inflate the layout for this fragment
        binding = FragmentListRecordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val dataSource = RecordDatabase.getInstance(application).recordDatabaseDao

        viewModel = ViewModelProvider(this, ListRecordViewModelFactory(dataSource)).get(
            ListFragmentViewModel::class.java)

        setupData()

        binding?.recycler?.adapter = adapter
        binding?.recycler?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupData() {
        viewModel.records.observe(viewLifecycleOwner, {
            adapter.data = it

        })
    }

}