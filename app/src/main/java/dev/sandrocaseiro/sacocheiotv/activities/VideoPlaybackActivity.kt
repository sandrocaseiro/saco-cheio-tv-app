package dev.sandrocaseiro.sacocheiotv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.fragments.VideoPlaybackFragment

class VideoPlaybackActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.video_fragment, VideoPlaybackFragment())
                .commitNow()
        }
    }

    companion object {
        const val MEDIA_URL = "Media URL"
        const val MEDIA_TYPE = "Media Type"
        const val EPISODE = "Episode"
    }
}
