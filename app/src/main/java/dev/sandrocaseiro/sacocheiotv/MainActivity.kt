package dev.sandrocaseiro.sacocheiotv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.sandrocaseiro.sacocheiotv.fragments.LoginFragment

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val authHash = prefs.getString(Constants.AUTH_HASH, null)
        val fragment = if (!authHash.isNullOrEmpty()) MainFragment() else LoginFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, fragment)
                    .commitNow()
        }
    }
}