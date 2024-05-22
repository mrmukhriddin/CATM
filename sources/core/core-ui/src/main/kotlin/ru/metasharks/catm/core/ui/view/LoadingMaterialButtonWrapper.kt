package ru.metasharks.catm.core.ui.view

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.CircularProgressIndicator
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.utils.dpToPx
import kotlin.math.roundToInt

class LoadingMaterialButtonWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    var text: String? = null
        set(value) {
            field = value
            wrappedButton.text = field
        }

    private var decreaseSize = 0f

    init {
        getValuesFromAttrs(attrs)
        layoutTransition = LayoutTransition()
    }

    val wrappedButton: Button
        get() = children.first { it is Button } as Button

    private val progressIndicator: CircularProgressIndicator?
        get() = children.firstOrNull { it is CircularProgressIndicator } as CircularProgressIndicator?

    private var textColorStateList: ColorStateList? = null

    var isLoading: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                showLoading()
            } else {
                hideLoading()
            }
        }

    override fun setEnabled(enabled: Boolean) {
        wrappedButton.isEnabled = enabled
        super.setEnabled(enabled)
    }

    fun setText(@StringRes stringRes: Int) {
        text = context.getString(stringRes)
    }

    private fun hideLoading() {
        addProgressIndicatorIfNeeded()
        requireNotNull(progressIndicator).isGone = true
        wrappedButton.setTextColor(requireNotNull(textColorStateList))
        wrappedButton.isClickable = true
    }

    private fun showLoading() {
        wrappedButton.isClickable = false
        if (textColorStateList == null) {
            textColorStateList = wrappedButton.textColors
        }
        addProgressIndicatorIfNeeded()
        requireNotNull(progressIndicator).isVisible = true
        wrappedButton.setTextColor(solidColor)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val childButton = wrappedButton
        childButton.setOnClickListener(l)
    }

    fun setOnClickListenerAndLoad(l: OnClickListener?) {
        if (l == null) return super.setOnClickListener(l)
        val onClickListener = OnClickListener {
            isLoading = true
            l.onClick(it)
        }
        setOnClickListener(onClickListener)
    }

    private fun addProgressIndicatorIfNeeded() {
        if (progressIndicator == null) {
            addView(createProgressIndicator())
        }
    }

    private fun createProgressIndicator(): CircularProgressIndicator =
        CircularProgressIndicator(context).apply {
            isIndeterminate = true
            layoutParams = generateDefaultLayoutParams().apply {
                gravity = Gravity.CENTER
            }
            indicatorSize = this@LoadingMaterialButtonWrapper.height - decreaseSize.roundToInt()
            trackThickness = context.dpToPx(STROKE_THICKNESS_DP)
            setIndicatorColor(requireNotNull(textColorStateList?.defaultColor))
        }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            BUNDLE_SUPER_STATE to super.onSaveInstanceState(),
            BUNDLE_TEXT_COLOR to textColorStateList
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) return super.onRestoreInstanceState(state)
        var superState: Parcelable? = null
        if (state is Bundle) {
            textColorStateList = state.getParcelable(BUNDLE_TEXT_COLOR)
            superState = state.getParcelable(BUNDLE_SUPER_STATE)
        }
        super.onRestoreInstanceState(superState)
    }

    private fun getValuesFromAttrs(attrs: AttributeSet?) {

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingMaterialButtonWrapper)
        decreaseSize = typedArray.getDimension(
            R.styleable.LoadingMaterialButtonWrapper_progressIndicatorSizeSubtrahend,
            0f
        )
        typedArray.recycle()
    }

    companion object {
        private const val BUNDLE_SUPER_STATE = "superState"
        private const val BUNDLE_TEXT_COLOR = "textColor"

        private const val STROKE_THICKNESS_DP = 4
    }
}
