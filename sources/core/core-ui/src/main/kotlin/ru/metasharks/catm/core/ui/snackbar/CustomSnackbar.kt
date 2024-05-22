package ru.metasharks.catm.core.ui.snackbar

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import com.google.android.material.snackbar.BaseTransientBottomBar
import ru.metasharks.catm.core.ui.databinding.CustomSnackbarBinding
import ru.metasharks.catm.utils.layoutInflater

class CustomSnackbar private constructor(
    parent: ViewGroup,
    content: View,
    contentViewCallback: com.google.android.material.snackbar.ContentViewCallback =
        DefaultContentViewCallback(content)
) : BaseTransientBottomBar<CustomSnackbar>(
    parent, content, contentViewCallback
) {

    private var onClickListener: View.OnClickListener? = null

    init {
        getView().setBackgroundColor(Color.TRANSPARENT)
        val container = (getView() as SnackbarBaseLayout)
        container.children
    }

    fun setOnClickListener(onClickListener: View.OnClickListener): CustomSnackbar {
        this.onClickListener = onClickListener
        return this
    }

    companion object {

        fun make(view: View, @StringRes messageTextResId: Int, duration: Int): CustomSnackbar {
            return make(view, view.context.getString(messageTextResId), duration)
        }

        fun make(view: View, messageText: String, length: Int): CustomSnackbar {
            val parent = view.findSuitableParent()
                ?: throw IllegalArgumentException("No suitable parent found for this view")
            val binding = CustomSnackbarBinding.inflate(view.context.layoutInflater, parent, false)
            val snackbar = CustomSnackbar(parent, binding.root).apply {
                duration = length
            }
            binding.apply {
                message.text = messageText
                root.setOnClickListener {
                    snackbar.dismiss()
                    snackbar.onClickListener?.onClick(it)
                }
            }
            return snackbar
        }

        // Код из исходного кода класса Snackbar
        private fun View?.findSuitableParent(): ViewGroup? {
            var view = this
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    // Нашли CoordinatorLayout, используем его
                    return view
                } else if (view is FrameLayout) {
                    if (view.id == android.R.id.content) {
                        // Если мы напали на decor content view, то мы не нашли CoordinatorLayout
                        // в иерархии, поэтому используем его
                        return view
                    } else {
                        // Если это не content view, то используем его как fallback
                        fallback = view
                    }
                }

                if (view != null) {
                    // Иначе, мы идем дальше вверх по иерархии и пытаемся найти родителя
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)

            // Если мы не находим ни CoL, ни Content View - то используем fallback
            return fallback
        }
    }
}
