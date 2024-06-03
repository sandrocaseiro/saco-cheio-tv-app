package dev.sandrocaseiro.sacocheiotv.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.activities.EpisodeDetailsActivity
import dev.sandrocaseiro.sacocheiotv.activities.VideoPlaybackActivity
import dev.sandrocaseiro.sacocheiotv.models.viewmodels.VideoPlaybackViewModel
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import dev.sandrocaseiro.sacocheiotv.services.EpisodeScreenSaverService
import java.util.Date
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import androidx.media3.ui.R as UIR


class VideoPlaybackFragment : Fragment() {

    private lateinit var mHandler: Handler
    private lateinit var mPlayer: ExoPlayer
    private lateinit var mPlayerView: PlayerView
    private var mSavePositionPendingIntent: PendingIntent? = null
    private lateinit var mEpisode: VEpisode

    //    TODO: Check the right way to do this
    private val vm = VideoPlaybackViewModel()

    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.video_fragment, container, false)

        mPlayerView = view.findViewById(R.id.player_view)
        mPlayerView.requestFocus()

        val customTimeBar: TimeBar = mPlayerView.findViewById(UIR.id.exo_progress)
        customTimeBar.setKeyTimeIncrement(SEEK_STEP)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                @OptIn(UnstableApi::class)
                override fun handleOnBackPressed() {
                    if (mPlayerView.isControllerFullyVisible) {
                        mPlayerView.hideController()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })

        return view
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEpisode =
            activity?.intent?.getSerializableExtra(EpisodeDetailsActivity.EPISODE) as VEpisode
        val videoUrl = activity?.intent?.getStringExtra(VideoPlaybackActivity.VIDEO_URL)

        Log.i(TAG, "Video URL: $videoUrl")

        val mediaItem =
            MediaItem.Builder().setUri(Uri.parse(videoUrl)).setMimeType(MimeTypes.APPLICATION_M3U8)
                .build()

        val factory = DefaultHttpDataSource.Factory().apply {
            setDefaultRequestProperties(mapOf("referer" to "https://sacocheio.tv/"))
        }

        val mediaSource = HlsMediaSource.Factory(factory).createMediaSource(mediaItem)

        mPlayer = ExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(SEEK_STEP)
            .setSeekForwardIncrementMs(SEEK_STEP)
            .build().apply {
                setMediaSource(mediaSource)
                addListener(PlayerListener())
                playWhenReady = true
                if (mEpisode.timeWatched > 0) {
                    seekTo(mEpisode.timeWatched)
                }

                prepare()
            }
        mHandler = Handler(mPlayer.applicationLooper)

        mPlayerView.player = mPlayer
    }

    override fun onPause() {
        super.onPause()
        mPlayer.pause()
    }

    override fun onDestroy() {
        stopTimer()
        mPlayer.release()

        super.onDestroy()
    }

    private fun startTimer() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val filter = IntentFilter()
        filter.addAction(BROADCAST_ACTION)
        requireActivity().registerReceiver(mBroadcastReceiver, filter)

        val alarms = requireContext().getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(BROADCAST_ACTION)
        mSavePositionPendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarms.setRepeating(
            AlarmManager.RTC_WAKEUP,
            Date().time,
            BACKGROUND_UPDATE_DELAY,
            mSavePositionPendingIntent!!
        )
    }

    private fun stopTimer() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val alarms = requireContext().getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager
        if (mSavePositionPendingIntent != null) {
            alarms.cancel(mSavePositionPendingIntent!!)
            mSavePositionPendingIntent = null
            requireActivity().unregisterReceiver(mBroadcastReceiver)
        }
    }

    private fun startScreensaver() {
        // Trigger the screensaver after a delay
        Log.d(TAG, "Registering screen saver")
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "Starting screen saver")
            requireActivity().startService(
                Intent(
                    requireContext(),
                    EpisodeScreenSaverService::class.java
                )
            )
        }, SCREENSAVER_DELAY)
    }

    private fun stopScreensaver() {
        Log.d(TAG, "Stopping screen saver")
        requireActivity().stopService(
            Intent(
                requireContext(),
                EpisodeScreenSaverService::class.java
            )
        )
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BROADCAST_ACTION) {
                mHandler.post {
                    Log.d(TAG, "Saving time watched: ${mPlayer.contentPosition}")
                    vm.updateWatchTime(
                        mEpisode.showId,
                        mEpisode.id,
                        mPlayer.contentDuration,
                        mPlayer.contentPosition,
                        requireContext()
                    )
                }
            }
        }
    }

    private inner class PlayerListener : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                startTimer()
//                stopScreensaver()
            } else {
                stopTimer()
//                startScreensaver()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, error.toString())
            val cause = error.cause
            if (cause is HttpDataSource.HttpDataSourceException) {
                // An HTTP error occurred.
                val httpError = cause
                // It's possible to find out more about the error both by casting and by querying
                // the cause.
                if (httpError is HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code, message
                    // and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause, although
                    // note that it may be null.
                }
            }
        }
    }

    companion object {
        private const val TAG = "VideoPlaybackFragment"
        private const val BROADCAST_ACTION = "SAVE_VIDEO_POSITION"

        private val SEEK_STEP = 10.seconds.inWholeMilliseconds
        private val BACKGROUND_UPDATE_DELAY = 1.minutes.inWholeMilliseconds
        private val SCREENSAVER_DELAY = 5.seconds.inWholeMilliseconds
    }
}
