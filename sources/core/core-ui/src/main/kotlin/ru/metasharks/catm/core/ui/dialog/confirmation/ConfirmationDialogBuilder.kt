package ru.metasharks.catm.core.ui.dialog.confirmation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.databinding.DialogConfirmationBinding
import ru.metasharks.catm.utils.layoutInflater

typealias OnButtonClick = (View) -> Unit

class ConfirmationDialogBuilder(private val context: Context) {

    private var title: String? = null
    private var titleTextSize: Float? = null
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var onPositiveButtonClickListener: OnButtonClick? = null
    private var onNegativeButtonClickListener: OnButtonClick? = null
    private var showNegativeButton: Boolean? = null
    private var showPositiveButton: Boolean? = null

    fun build(): Dialog {
        val view =
            context.layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val dialogBuilder = AlertDialog.Builder(context)
        val binding = DialogConfirmationBinding.bind(view)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        with(binding) {
            if (title != null) {
                cdTitle.isVisible = true
                cdTitle.text = title
                if (titleTextSize != null) {
                    cdTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, requireNotNull(titleTextSize))
                }
            } else {
                cdTitle.isGone = true
            }
            if (message != null) {
                cdMessage.isVisible = true
                cdMessage.text = message
            } else {
                cdMessage.isGone = true
            }
            if (showPositiveButton == false) {
                cdPrimaryAction.isGone = true
            } else {
                cdPrimaryAction.text = positiveButtonText
                cdPrimaryAction.isVisible = true
                cdPrimaryAction.setOnClickListener {
                    onPositiveButtonClickListener?.invoke(it)
                    dialog.dismiss()
                }
            }
            if (showNegativeButton == false) {
                cdSecondaryAction.isGone = true
            } else {
                cdSecondaryAction.setOnClickListener {
                    onNegativeButtonClickListener?.invoke(it)
                    dialog.dismiss()
                }
                cdSecondaryAction.text = negativeButtonText
            }
        }
        return dialog
    }

    fun show() {
        build().show()
    }

    fun setTitleTextSize(@DimenRes dimenRes: Int): ConfirmationDialogBuilder {
        return setTitleTextSize(context.resources.getDimension(dimenRes))
    }

    fun setTitleTextSize(textSizeSp: Float): ConfirmationDialogBuilder {
        this.titleTextSize = textSizeSp
        return this
    }

    fun setTitle(@StringRes stringId: Int): ConfirmationDialogBuilder {
        return setTitle(context.getString(stringId))
    }

    fun setTitle(title: String): ConfirmationDialogBuilder {
        this.title = title
        return this
    }

    fun setMessage(@StringRes stringId: Int): ConfirmationDialogBuilder {
        return setMessage(context.getString(stringId))
    }

    fun setMessage(message: String): ConfirmationDialogBuilder {
        this.message = message
        return this
    }

    fun setPositiveButtonText(@StringRes stringId: Int): ConfirmationDialogBuilder {
        return setPositiveButtonText(context.getString(stringId))
    }

    fun setPositiveButtonText(positiveText: String): ConfirmationDialogBuilder {
        this.positiveButtonText = positiveText
        return this
    }

    fun setNegativeButtonText(@StringRes stringId: Int): ConfirmationDialogBuilder {
        return setNegativeButtonText(context.getString(stringId))
    }

    fun setNegativeButtonText(negativeText: String): ConfirmationDialogBuilder {
        this.negativeButtonText = negativeText
        return this
    }

    fun setOnPositiveButtonAction(listener: OnButtonClick): ConfirmationDialogBuilder {
        onPositiveButtonClickListener = listener
        return this
    }

    fun setOnNegativeButtonAction(listener: OnButtonClick): ConfirmationDialogBuilder {
        onNegativeButtonClickListener = listener
        return this
    }

    fun setNegativeButtonShow(show: Boolean): ConfirmationDialogBuilder {
        showNegativeButton = show
        return this
    }

    fun setPositiveButtonShow(show: Boolean): ConfirmationDialogBuilder {
        showPositiveButton = show
        return this
    }
}
