package dev.sandrocaseiro.sacocheiotv.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.drawable.Drawable
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.OnActionClickedListener
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.sandrocaseiro.sacocheiotv.Constants
import dev.sandrocaseiro.sacocheiotv.MainActivity
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.activities.EpisodeDetailsActivity
import dev.sandrocaseiro.sacocheiotv.activities.VideoPlaybackActivity
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.viewmodels.EpisodeDetailsViewModel
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisodeMedia
import dev.sandrocaseiro.sacocheiotv.presenters.EpisodeDetailsPresenter

/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class EpisodeDetailsFragment : DetailsSupportFragment() {

    private var mSelectedEpisode: VEpisode? = null
    private var mEpisode: EEpisode? = null
    private var mMediaUrls: Map<VEpisodeMedia, String> = mapOf()
    private var mIsWatched: Boolean = false

    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mActionAdapter: ArrayObjectAdapter

    private lateinit var mPrefs: SharedPreferences
    private val vm = EpisodeDetailsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)
        mPrefs = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)

        mSelectedEpisode =
            requireActivity().intent.getSerializableExtra(EpisodeDetailsActivity.EPISODE) as VEpisode
        if (mSelectedEpisode != null) {
            mPresenterSelector = ClassPresenterSelector()
            mAdapter = ArrayObjectAdapter(mPresenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            adapter = mAdapter
            initializeBackground(mSelectedEpisode)

            vm.watched.value = mSelectedEpisode!!.isWatched
            mIsWatched = mSelectedEpisode!!.isWatched

            observeViewModel()
            vm.getEpisode(mSelectedEpisode!!.showId, mSelectedEpisode!!.id, requireContext())
            vm.getEpisodeMedia(mPrefs.getString(Constants.AUTH_HASH, "")!!, mSelectedEpisode!!.showId, mSelectedEpisode!!.slug)
        } else {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart")

        vm.getEpisode(mSelectedEpisode!!.showId, mSelectedEpisode!!.id, requireContext())
    }

    private fun observeViewModel() {
        vm.episode.observe(this) {
            mEpisode = it
            mSelectedEpisode!!.timeWatched = it.timeWatched
            mSelectedEpisode!!.totalTime = it.totalTime
            mSelectedEpisode!!.isWatched = it.isWatched
            if (mIsWatched != it.isWatched) {
                mIsWatched = it.isWatched
                updateActions()
            }

            (mAdapter[0] as DetailsOverviewRow).item = mSelectedEpisode
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
        }

        vm.media.observe(this) {
            mMediaUrls = it
            updateActions()
        }

        vm.watched.observe(this) {
            mIsWatched = it
            updateActions()
        }
    }

    private fun updateActions() {
        mActionAdapter.clear()
        mActionAdapter.add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_1),
                if (mMediaUrls[VEpisodeMedia.VIDEO] != null) resources.getString(R.string.watch_2) else resources.getString(R.string.watch_unavailable)
            )
        )
        mActionAdapter.add(
            Action(
                ACTION_LISTEN,
                resources.getString(R.string.listen_1),
                if (mMediaUrls[VEpisodeMedia.AUDIO] != null) resources.getString(R.string.listen_2) else resources.getString(R.string.listen_unavailable)
            )
        )
        mActionAdapter.add(
            Action(
                ACTION_MARK_AS,
                resources.getString(R.string.mark_as_1),
                if (!mIsWatched) resources.getString(R.string.mark_as_watched) else resources.getString(R.string.mark_as_unwatched)
            )
        )

        mActionAdapter.notifyArrayItemRangeChanged(0,  mActionAdapter.size())
    }

    private fun initializeBackground(episode: VEpisode?) {
        mDetailsBackground.enableParallax()
        Glide.with(requireActivity())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .load(episode?.imageUrl)
                .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap,
                                                 transition: Transition<in Bitmap>?) {
                        mDetailsBackground.coverBitmap = bitmap
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                    }
                })
    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedEpisode?.toString())
        val row = DetailsOverviewRow(mSelectedEpisode)
        row.imageDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.default_background)
        val width = convertDpToPixel(requireActivity(), DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(requireActivity(), DETAIL_THUMB_HEIGHT)
        Glide.with(requireContext())
            .load(mSelectedEpisode?.imageUrl)
            .fitCenter()
            .error(R.drawable.default_background)
            .into<SimpleTarget<Drawable>>(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    drawable: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    Log.d(TAG, "details overview card image url ready: $drawable")
                    row.imageDrawable = drawable
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })

        mActionAdapter = ArrayObjectAdapter()
        row.actionsAdapter = mActionAdapter

        mAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(EpisodeDetailsPresenter())
        detailsPresenter.backgroundColor =
            ContextCompat.getColor(requireActivity(), R.color.selected_background)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
                activity, EpisodeDetailsActivity.SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            if (action.id == ACTION_WATCH || action.id == ACTION_LISTEN) {
                if (mMediaUrls.isEmpty()) {
                    Toast.makeText(requireActivity(), "Waiting for media information", Toast.LENGTH_SHORT).show()
                    return@OnActionClickedListener
                }
                
                val mediaType = if (action.id == ACTION_WATCH) VEpisodeMedia.VIDEO else VEpisodeMedia.AUDIO

                val intent = Intent(requireActivity(), VideoPlaybackActivity::class.java)
                intent.putExtra(VideoPlaybackActivity.EPISODE, mSelectedEpisode)
                intent.putExtra(VideoPlaybackActivity.MEDIA_URL, mMediaUrls[mediaType])
                intent.putExtra(VideoPlaybackActivity.MEDIA_TYPE, mediaType)
                startActivity(intent)
            }
            else if (action.id == ACTION_MARK_AS) {
                vm.toggleEpisodeWatched(mSelectedEpisode!!.showId, mSelectedEpisode!!.id, requireContext())
            }
        }
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

//    TODO: Make as external function
    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    companion object {
        private const val TAG = "EpisodeDetailsFragment"

        private const val ACTION_WATCH = 1L
        private const val ACTION_LISTEN = 3L
        private const val ACTION_MARK_AS = 2L

        private const val DETAIL_THUMB_WIDTH = 474
        private const val DETAIL_THUMB_HEIGHT = 474
    }
}
