package org.neidhardt.dynamicsoundboard.mediaplayer

import android.os.Handler
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by eric.neidhardt@sevenval.com on 21.11.2016.
 */
private const val UPDATE_INTERVAL: Long = 500

class ProgressMonitor(private val exoPlayer: ExoMediaPlayer) {

	var onProgressChangedEventListener: MediaPlayerController.OnProgressChangedEventListener? = null

	private val handler: Handler = Handler()
	private val hasTimerStarted: AtomicBoolean = AtomicBoolean(false)

	private val triggerProgressChanged: Runnable = Runnable {
		val progress = this.exoPlayer.progress

		if (!this.exoPlayer.isDeletionPending) {
			this.onProgressChangedEventListener?.onProgressChanged(this.exoPlayer, progress, exoPlayer.trackDuration)
		}

		this.hasTimerStarted.set(false)
		this.startProgressUpdateTimer()
	}

	fun startProgressUpdateTimer() {
		if (!this.hasTimerStarted.getAndSet(true))
			this.handler.postDelayed(this.triggerProgressChanged, UPDATE_INTERVAL)
	}

	fun stopProgressUpdateTimer() {
		if (this.hasTimerStarted.getAndSet(false))
			this.handler.removeCallbacks(this.triggerProgressChanged)
	}
}