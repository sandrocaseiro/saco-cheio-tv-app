package dev.sandrocaseiro.sacocheiotv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.fragments.EpisodeDetailsFragment

class EpisodeDetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, EpisodeDetailsFragment())
                    .commitNow()
        }
    }

    companion object {
        const val EPISODE = "Episode"
        const val SHARED_ELEMENT_NAME = "hero"
    }
}
