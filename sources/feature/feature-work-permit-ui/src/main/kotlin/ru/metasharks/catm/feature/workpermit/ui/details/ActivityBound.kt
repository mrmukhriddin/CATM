package ru.metasharks.catm.feature.workpermit.ui.details

import android.app.Activity
import androidx.annotation.StringRes
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.dialog.confirmation.OnButtonClick

open class ActivityBound<T : Activity> {

    protected var mActivity: T? = null
    protected val activity: T
        get() = requireNotNull(mActivity)

    open fun init(activity: T) {
        this.mActivity = activity
    }

    open fun clear() {
        this.mActivity = null
    }

    protected fun showDialog(
        @StringRes titleRes: Int,
        @StringRes negativeBtnTextRes: Int,
        @StringRes positiveBtnTextRes: Int,
        @StringRes messageRes: Int? = null,
        onButtonClick: OnButtonClick? = null
    ) {
        val builder = ConfirmationDialogBuilder(activity)
            .setTitle(titleRes)
            .setNegativeButtonText(negativeBtnTextRes)
            .setPositiveButtonText(positiveBtnTextRes)
            .setOnPositiveButtonAction {
                onButtonClick?.invoke(it)
            }
        if (messageRes != null) {
            builder.setMessage(messageRes)
        }
        builder.show()
    }
}
