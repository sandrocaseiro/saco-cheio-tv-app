package dev.sandrocaseiro.sacocheiotv.views

import androidx.leanback.R
import android.R as AR
import dev.sandrocaseiro.sacocheiotv.R as SCR
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.leanback.widget.BaseCardView

open class ImageCardView(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.imageCardViewStyle
) :
    BaseCardView(context, attrs, defStyleAttr) {
    /**
     * Returns the main image view.
     */
    var mainImageView: ImageView? = null
        private set
    private var mImageArea: ViewGroup? = null
    private var mInfoArea: ViewGroup? = null
    private var mTitleView: TextView? = null
    private var mContentView: TextView? = null
    private var mCheckImage: ImageView? = null
    private var mProgressContainer: ConstraintLayout? = null
    private var mAttachedToWindow = false
    private var mFadeInAnimator: ObjectAnimator? = null
    private var mLoadingIndicator: ProgressBar? = null

    init {
        buildImageCardView(attrs, defStyleAttr, R.style.Widget_Leanback_ImageCardView)
    }

    private fun buildImageCardView(attrs: AttributeSet?, defStyleAttr: Int, defStyle: Int) {
        // Make sure the ImageCardView is focusable.
        isFocusable = true
        isFocusableInTouchMode = true

        val inflater = LayoutInflater.from(context)
        inflater.inflate(SCR.layout.image_card_view, this)
        val cardAttrs = context.obtainStyledAttributes(
            attrs,
            R.styleable.lbImageCardView, defStyleAttr, defStyle
        )

        mLoadingIndicator = findViewById(SCR.id.loading_indicator)

        mainImageView = findViewById(R.id.main_image)
        mTitleView = findViewById(R.id.title_text)
        mContentView = findViewById(R.id.content_text)
        mImageArea = findViewById(SCR.id.image_field)
        mInfoArea = findViewById(R.id.info_field)
        mCheckImage = findViewById(SCR.id.check_image)
        mProgressContainer = findViewById(SCR.id.progress_container)

        // Set Object Animator for image view.
        mFadeInAnimator = ObjectAnimator.ofFloat(mainImageView, ALPHA, 1f)
        mainImageView?.resources?.getInteger(AR.integer.config_shortAnimTime)?.let {
            mFadeInAnimator?.setDuration(
                it.toLong()
            )
        }

        cardAttrs.recycle()
    }

    /**
     * Sets the image drawable with optional fade-in animation.
     */
    fun setMainImage(drawable: Drawable?, fade: Boolean) {
        if (mainImageView == null) {
            return
        }

        mainImageView!!.setImageDrawable(drawable)
        if (drawable == null) {
            mFadeInAnimator!!.cancel()
            mainImageView!!.alpha = 1f
            mainImageView!!.visibility = INVISIBLE
        } else {
            mainImageView!!.visibility = VISIBLE
            if (fade) {
                fadeIn()
            } else {
                mFadeInAnimator!!.cancel()
                mainImageView!!.alpha = 1f
            }
        }
    }

    /**
     * Sets the layout dimensions of the ImageView.
     */
    fun setMainImageDimensions(width: Int, height: Int) {
        val lp2 = mImageArea!!.layoutParams
        lp2.width = width
        lp2.height = height
        mImageArea!!.layoutParams = lp2

        val lp = mainImageView!!.layoutParams
        lp.width = width
        lp.height = height
        mainImageView!!.layoutParams = lp
    }

    fun setLoadingIndicatorDimensions(width: Int, height: Int) {
        val lp = mLoadingIndicator!!.layoutParams
        lp.width = width
        lp.height = height
        mLoadingIndicator!!.layoutParams = lp
    }

    fun showLoading() {
        mLoadingIndicator?.visibility = VISIBLE
        mImageArea?.visibility = GONE
        mInfoArea?.visibility = GONE
    }

    fun showContent() {
        mLoadingIndicator?.visibility = GONE
        mImageArea?.visibility = VISIBLE
        mInfoArea?.visibility = VISIBLE
    }

    var mainImage: Drawable?
        /**
         * Returns the ImageView drawable.
         */
        get() {
            if (mainImageView == null) {
                return null
            }

            return mainImageView!!.drawable
        }
        /**
         * Sets the image drawable with fade-in animation.
         */
        set(drawable) {
            setMainImage(drawable, true)
        }

    var titleText: CharSequence?
        /**
         * Returns the title text.
         */
        get() {
            if (mTitleView == null) {
                return null
            }

            return mTitleView!!.text
        }
        /**
         * Sets the title text.
         */
        set(text) {
            if (mTitleView == null) {
                return
            }
            mTitleView!!.text = text
        }

    var contentText: CharSequence?
        /**
         * Returns the content text.
         */
        get() {
            if (mContentView == null) {
                return null
            }

            return mContentView!!.text
        }
        /**
         * Sets the content text.
         */
        set(text) {
            if (mContentView == null) {
                return
            }
            mContentView!!.text = text
        }

    var isWatched: Boolean
        get() {
            if (mCheckImage == null)
                return false

            return mCheckImage!!.visibility == VISIBLE
        }
        set(value) {
            if (mCheckImage == null)
                return

            mCheckImage!!.visibility = if (value) VISIBLE else GONE
        }

    var progress: Float = 0.0F
        set(value) {
            if (mProgressContainer == null)
                return

            if (value == 0.0F) {
                mProgressContainer!!.visibility = GONE
                return
            }
            else
                mProgressContainer!!.visibility = VISIBLE

            val cSet = ConstraintSet()
            cSet.clone(mProgressContainer)

            cSet.constrainPercentWidth(SCR.id.progress_bar, value / 100)
            cSet.applyTo(mProgressContainer)
        }

    private fun fadeIn() {
        mainImageView!!.alpha = 0f
        if (mAttachedToWindow) {
            mFadeInAnimator!!.start()
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAttachedToWindow = true
        if (mainImageView!!.alpha == 0f) {
            fadeIn()
        }
    }

    override fun onDetachedFromWindow() {
        mAttachedToWindow = false
        mFadeInAnimator!!.cancel()
        mainImageView!!.alpha = 1f
        super.onDetachedFromWindow()
    }

    companion object {
        private const val ALPHA = "alpha"
    }
}
