package com.light.voicerecorder.ui.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.lang.IllegalArgumentException

class PlayerViewModel(itemPath: String, application: Application) : AndroidViewModel(application),
	LifecycleObserver {

	private val _player = MutableLiveData<Player?>()
	val player: LiveData<Player?> = _player
	private var contentPosition = 0L
	private var playWhenReady = true
	var itemPath: String? = itemPath

	init {
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	fun onForegrounded(){
		setupPlayer()
	}
	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	fun onBackgrounded(){
		releaseExoPlayer()
	}

	//инициализация и настройка плеера
	private fun setupPlayer() {
		val dataSourceFactory = DefaultDataSourceFactory(
			getApplication(),
			Util.getUserAgent(getApplication(), RECORDER)
		)

		val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
			.setExtractorsFactory(DefaultExtractorsFactory())
			.createMediaSource(Uri.parse(itemPath))

		val player = SimpleExoPlayer.Builder(getApplication()).build()
		player.prepare(mediaSource)
		player.playWhenReady = playWhenReady
		player.seekTo(contentPosition)

		this._player.value = player
	}

	private fun releaseExoPlayer() {
		val player = _player.value ?: return
		this._player.value = null

		contentPosition = player.contentPosition
		playWhenReady = player.playWhenReady
		player.release()
	}

	override fun onCleared() {
		super.onCleared()

		releaseExoPlayer()
		ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
	}

	companion object {
		const val RECORDER = "recorder"
	}
}

class PlayerViewModelFactory(
	private val mediaPath: String,
	private var application: Application
): ViewModelProvider.Factory{
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
			return PlayerViewModel(mediaPath, application) as T
		}
		throw IllegalArgumentException("unknown ViewModel class")
	}
}