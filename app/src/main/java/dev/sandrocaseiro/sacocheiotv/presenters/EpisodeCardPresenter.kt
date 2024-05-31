package dev.sandrocaseiro.sacocheiotv.presenters

import android.graphics.drawable.Drawable
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.ViewGroup

import com.bumptech.glide.Glide
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import dev.sandrocaseiro.sacocheiotv.views.ImageCardView
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class EpisodeCardPresenter : Presenter() {
    private var mDefaultCardImage: Drawable? = null
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
//        Log.d(TAG, "onCreateViewHolder")

        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.selected_background)
        mDefaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.app_icon)

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.setLoadingIndicatorDimensions(CARD_WIDTH, CARD_WIDTH)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
//        Log.d(TAG, "onBindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        if (item == null) {
            cardView.showLoading()
            return
        }

        cardView.showContent()

        val episode = item as VEpisode
        if (cardView.mainImageView != null) {
            cardView.titleText = episode.name
            cardView.contentText = episode.description
            cardView.isWatched = episode.isWatched
            cardView.progress = episode.watchedPercentage
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            Glide.with(viewHolder.view.context)
                    .load(episode.imageUrl)
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.mainImageView!!)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
//        Log.d(TAG, "onUnbindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
//        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
//        view.setInfoAreaBackgroundColor(color)
    }

    companion object {
        private const val TAG = "EpisodeCardPresenter"

        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
    }
}