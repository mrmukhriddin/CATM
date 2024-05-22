package ru.metasharks.catm.feature.createworkpermit.ui.steps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.fragment.requireListener

abstract class StepFragment(layoutRes: Int) : Fragment(layoutRes) {

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val isKeyboardShown = intent.getBooleanExtra(BaseActivity.EXTRA_VISIBLE, false)
            if (isKeyboardShown) {
                onKeyboardShown()
            } else {
                onKeyboardHidden()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            receiver,
            IntentFilter(BaseActivity.BROADCAST_KEYBOARD_VISIBILITY)
        )
    }

    override fun onPause() {
        requireActivity().unregisterReceiver(receiver)
        super.onPause()
    }

    protected open fun onKeyboardShown() = Unit

    protected open fun onKeyboardHidden() = Unit

    protected lateinit var stepCallback: StepCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        stepCallback = requireListener()
    }

    companion object {

        const val ARG_RESTORE_DATA = "restore_data"
    }
}
