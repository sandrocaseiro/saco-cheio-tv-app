package dev.sandrocaseiro.sacocheiotv.services

import android.service.dreams.DreamService
import android.util.Log
import dev.sandrocaseiro.sacocheiotv.R

class EpisodeScreenSaverService : DreamService() {
    override fun onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow")
        super.onAttachedToWindow()

        isInteractive = false
        isFullscreen = true
        setContentView(R.layout.episode_dream)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
    }

    companion object {
        private const val TAG = "EpisodeScreenSaverSrvc"
    }
}
