package ru.metasharks.catm.core.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.databinding.WrapperLabeledBinding
import ru.metasharks.catm.core.ui.utils.measuredHeightWithMargins
import ru.metasharks.catm.core.ui.utils.measuredWidthWithMargins
import ru.metasharks.catm.utils.setDefinitePadding

class LabeledWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    var label: String? = null
        set(value) {
            field = value
            binding.label.text = field
        }

    var textColor: Int
        set(value) {
            if (value != -1) {
                binding.label.setTextColor(value)
            }
        }
        get() = binding.label.currentTextColor

    private val binding by lazy {
        WrapperLabeledBinding.bind(this)
    }

    override fun setEnabled(enabled: Boolean) {
        children.forEach { child ->
            setEnabled(child, enabled)
        }
        super.setEnabled(enabled)
    }

    fun setEnabled(view: View, enabled: Boolean) {
        if (view is ViewGroup) {
            view.forEach { setEnabled(it, enabled) }
        }
        view.isEnabled = enabled
    }

    init {
        inflate(context, R.layout.wrapper_labeled, this)
        getValuesFromAttrs(attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var child: View = binding.label

        measureChildWithMargins(
            child,
            widthMeasureSpec, 0,
            heightMeasureSpec, 0
        )

        var totalHeight = child.measuredHeightWithMargins

        for (i in 1 until childCount) {
            child = getChildAt(i)
            if (child.isGone) {
                continue
            }
            measureChildWithMargins(
                child,
                widthMeasureSpec, 0,
                heightMeasureSpec, totalHeight
            )
            totalHeight += child.measuredHeightWithMargins
        }
        val contentHeight = totalHeight + paddingTop + paddingBottom
        val contentWidth = children.maxOf { it.measuredWidthWithMargins }
        setMeasuredDimension(
            resolveSize(contentWidth, widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var child: View = binding.label

        var leftPosition = paddingLeft + child.marginLeft
        var topPosition = paddingTop + child.marginTop
        var rightPosition = leftPosition + child.measuredWidth
        var bottomPosition = topPosition + child.measuredHeight

        binding.label.layout(leftPosition, topPosition, rightPosition, bottomPosition)

        for (i in 1 until childCount) {
            var previousMarginBottom = child.marginBottom
            child = getChildAt(i)
            if (child.isGone) {
                continue
            }
            leftPosition = paddingLeft + child.marginLeft
            topPosition = bottomPosition + child.marginTop + previousMarginBottom
            rightPosition = leftPosition + child.measuredWidth
            bottomPosition = topPosition + child.measuredHeight
            child.layout(leftPosition, topPosition, rightPosition, bottomPosition)
        }
    }

    private fun getValuesFromAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LabeledWrapper, defStyleAttr, 0)
        val paddingLeft = typedArray.getDimension(R.styleable.LabeledWrapper_labelPadding_left, 0f)
        val paddingRight =
            typedArray.getDimension(R.styleable.LabeledWrapper_labelPadding_right, 0f)
        val paddingBottom =
            typedArray.getDimension(R.styleable.LabeledWrapper_labelPadding_bottom, 0f)
        binding.label.setDefinitePadding(
            left = paddingLeft.toInt(),
            right = paddingRight.toInt(),
            bottom = paddingBottom.toInt(),
        )
        label = typedArray.getString(R.styleable.LabeledWrapper_label)
        val textAppearanceId =
            typedArray.getResourceId(
                R.styleable.LabeledWrapper_label_textAppearance,
                -1
            )
        textColor =
            typedArray.getResourceId(
                R.styleable.LabeledWrapper_label_textColor,
                -1
            ).let {
                if (it != -1) {
                    ContextCompat.getColor(context, it)
                } else {
                    -1
                }
            }
        if (textAppearanceId != -1) {
            binding.label.setTextAppearance(textAppearanceId)
        }
        typedArray.recycle()
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }
}
