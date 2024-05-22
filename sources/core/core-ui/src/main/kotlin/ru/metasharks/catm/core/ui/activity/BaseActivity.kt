package ru.metasharks.catm.core.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.metasharks.catm.core.license.LicenseExpiredProvider
import ru.metasharks.catm.core.navigation.ApplicationNavigator
import ru.metasharks.catm.core.ui.databinding.SnackbarFloatingBinding
import ru.metasharks.catm.utils.getRootView
import ru.metasharks.catm.utils.isKeyboardOpen
import ru.metasharks.catm.utils.layoutInflater
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    private var keyboardListenersAttached = false

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val intent = Intent(BROADCAST_KEYBOARD_VISIBILITY)
        val isShown = isKeyboardOpen()
        if (isShown) {
            onShowKeyboard()
        } else {
            onHideKeyboard()
        }
        intent.putExtra(EXTRA_VISIBLE, isShown)
        sendBroadcast(intent)
    }

    @Inject
    lateinit var applicationNavigator: ApplicationNavigator

    @Inject
    lateinit var licenseExpiredProvider: LicenseExpiredProvider

    fun requireSupportActionBar(): ActionBar {
        return requireNotNull(supportActionBar)
    }

    override fun setContentView(view: View) {
        if (licenseExpiredProvider()) {
            val rootContainer = FrameLayout(view.context)
            val licenseView = SnackbarFloatingBinding.inflate(view.context.layoutInflater)

            rootContainer.addView(view)
            rootContainer.addView(licenseView.root)

            super.setContentView(rootContainer)
        } else {
            super.setContentView(view)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationNavigator.registerActivity(this)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        if (toolbar == null) return super.setSupportActionBar(toolbar)
        setToolbar(toolbar)
    }

    fun setToolbar(
        toolbar: Toolbar,
        showNavigate: Boolean = true,
        showTitle: Boolean = false,
        title: String? = null
    ) {
        super.setSupportActionBar(toolbar)
        requireSupportActionBar().apply {
            if (showTitle) {
                setTitle(requireNotNull(title))
            }
            setDisplayHomeAsUpEnabled(showNavigate)
            setDisplayShowTitleEnabled(showTitle)
        }
    }

    fun setToolbar(
        toolbar: Toolbar,
        showTitle: Boolean,
        titleRes: Int,
        showNavigate: Boolean = true
    ) {
        super.setSupportActionBar(toolbar)
        requireSupportActionBar().apply {
            if (showTitle) {
                setTitle(titleRes)
            }
            setDisplayHomeAsUpEnabled(showNavigate)
            setDisplayShowTitleEnabled(showTitle)
        }
    }

    protected fun detachKeyboardListeners() {
        if (!keyboardListenersAttached) {
            return
        }
        getRootView().viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
    }

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }
        getRootView().viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        keyboardListenersAttached = true
    }

    override fun onDestroy() {
        detachKeyboardListeners()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    protected fun isKeyboardVisible(rootView: View): Boolean {
        return ViewCompat.getRootWindowInsets(rootView)?.isVisible(WindowInsetsCompat.Type.ime())
            ?: false
    }

    protected open fun onHideKeyboard() = Unit

    protected open fun onShowKeyboard() = Unit

    // You should override it to work with router back
    override fun onBackPressed() = Unit

    companion object {

        const val BROADCAST_KEYBOARD_VISIBILITY = "KEYBOARD_CHANGE_VISIBLE_STATE"
        const val EXTRA_VISIBLE = "visible"
    }
}
