package com.light.voicerecorder.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.light.voicerecorder.R
import com.light.voicerecorder.databinding.FragmentPlayerBinding

class PlayerFragment : DialogFragment(R.layout.fragment_player) {


    private lateinit var viewModel: PlayerViewModel
    private var itemPath: String? = null
    private var binding: FragmentPlayerBinding? = null

    companion object {
        private val ARG_ITEM_PATH = "recording_item_path"
    }

    fun newInstance(itemPath: String): PlayerFragment {
        val f = PlayerFragment()
        val b = Bundle()

        b.putString(ARG_ITEM_PATH, itemPath)
        f.arguments = b

        return f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        itemPath = arguments?.getString(ARG_ITEM_PATH)

        binding?.playerView?.showTimeoutMs = 0

        val application = requireNotNull(this.activity).application

        val viewModelFactory = itemPath?.let { PlayerViewModelFactory(it, application) }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)

        viewModel.itemPath = itemPath

        viewModel.player.observe(viewLifecycleOwner) {
            binding?.playerView?.player = it
        }
    }




}