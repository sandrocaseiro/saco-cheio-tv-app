package dev.sandrocaseiro.sacocheiotv

import java.util.Collections
import java.util.Timer
import java.util.TimerTask

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import dev.sandrocaseiro.sacocheiotv.views.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.sandrocaseiro.sacocheiotv.activities.EpisodeDetailsActivity
import dev.sandrocaseiro.sacocheiotv.models.viewmodels.MainViewModel
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import dev.sandrocaseiro.sacocheiotv.presenters.EpisodeCardPresenter

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseSupportFragment() {

    private val mHandler = Handler(Looper.myLooper()!!)
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null

    private val vm = MainViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onViewCreated(view, savedInstanceState)

        prepareBackgroundManager()
        setupUIElements()
        observeViewModel()
        loadRows()
        setupEventListeners()
        vm.getAllShows(requireContext())
    }

    override fun onResume() {
        super.onResume()

//        vm.getAllShows(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireActivity(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(requireActivity(), R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.search_opaque)
    }

//    TODO: Load episodes for each show. Need to find a way to save a reference of the show to update the corresponding row correctly
    private fun observeViewModel() {
        vm.shows.observe(viewLifecycleOwner) { shows ->
            (adapter as ArrayObjectAdapter).let {
                it.clear()
                for (show in shows) {
                    val cardPresenter = EpisodeCardPresenter()

                    val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                    val header = HeaderItem(1, show.name)
                    val listRow = ListRow(header, listRowAdapter)
                    it.add(listRow)
                    listRowAdapter.add(null)

                    vm.getAllEpisodes(show, listRowAdapter, requireContext())
                }
                it.notifyArrayItemRangeChanged(0, shows.size)
            }
        }
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
//        setOnSearchClickedListener {
//            Toast.makeText(requireActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
//                    .show()
//        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row) {

            if (item is VEpisode) {
                Log.d(TAG, "Item: $item")
                val intent = Intent(activity!!, EpisodeDetailsActivity::class.java)
                intent.putExtra(EpisodeDetailsActivity.EPISODE, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity!!,
                        (itemViewHolder.view as ImageCardView).mainImageView as View,
                        EpisodeDetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?,
                                    rowViewHolder: RowPresenter.ViewHolder, row: Row) {
//            TODO: Check if same show
            if (item is VEpisode) {
                Log.d(TAG, "Selected item $item")
                mBackgroundUri = item.showImageUrl
                startBackgroundTimer()
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(requireActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into<SimpleTarget<Drawable>>(
                        object : SimpleTarget<Drawable>(width, height) {
                            override fun onResourceReady(drawable: Drawable,
                                                         transition: Transition<in Drawable>?) {
                                mBackgroundManager.drawable = drawable
                            }
                        })
        mBackgroundTimer?.cancel()
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }

    companion object {
        private const val TAG = "MainFragment"

        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val GRID_ITEM_WIDTH = 200
        private const val GRID_ITEM_HEIGHT = 200
    }
}