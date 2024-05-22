package ru.metasharks.catm.core.ui.snackbar

import android.view.View
import com.google.android.material.snackbar.ContentViewCallback

class DefaultContentViewCallback(private val messageView: View) : ContentViewCallback {

    override fun animateContentIn(delay: Int, duration: Int) {
        messageView.alpha = 0f
        messageView.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong())
            .start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        messageView.alpha = 1f
        messageView.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong())
            .start()
    }
}
