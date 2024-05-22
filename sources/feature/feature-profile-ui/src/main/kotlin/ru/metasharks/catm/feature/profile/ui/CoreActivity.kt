package ru.metasharks.catm.feature.profile.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.network.internet.InternetListener
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.activity.KeyboardCallback
import ru.metasharks.catm.core.ui.activity.NotificationsWidgetCallback
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.core.ui.bottom.BottomNavigationController
import ru.metasharks.catm.core.ui.bottom.CoreScreenPage
import ru.metasharks.catm.feature.notifications.NotificationsService
import ru.metasharks.catm.feature.offline.OfflineListenerService
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.offline.pending.SendPendingActionsService
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.profile.ui.databinding.ActivityCoreBinding

@AndroidEntryPoint
internal class CoreActivity : BaseActivity(), ToolbarCallback, KeyboardCallback,
    NotificationsWidgetCallback {

    private val screensBackPressedActions: MutableMap<Int, (() -> Unit)> = mutableMapOf()

    private val viewModel: CoreViewModel by viewModels()
    private val binding: ActivityCoreBinding by viewBinding(CreateMethod.INFLATE)
    private lateinit var bottomNavigationController: BottomNavigationController

    private val internetListenerCallback = object : InternetListener.Callback {

        override fun onInternetAcquired() {
            runOnUiThread {
                if (viewModel.isInOfflineLiveData.value == true) {
                    showHasConnection(true)
                } else if (viewModel.isInOfflineLiveData.value == false) {
                    setOnlineStatus(true)
                }
            }
        }

        override fun onInternetLost() {
            runOnUiThread {
                if (viewModel.isInOfflineLiveData.value == false) {
                    showHasConnection(false)
                } else if (viewModel.isInOfflineLiveData.value == true) {
                    setOnlineStatus(false)
                }
            }
        }
    }

    private fun showHasConnection(hasConnection: Boolean) {
        with(binding.toolbarLayout.onlineStatus) {
            if (hasConnection) {
                text = getString(R.string.status_offline_has_connection)
                setOnClickListener {
                    viewModel.openOfflineSwitcherScreen(fromOnline = false, force = true)
                }
            } else {
                text = getString(R.string.status_online_no_connection)
                setOnClickListener {
                    viewModel.openOfflineSwitcherScreen(
                        fromOnline = true,
                        force = true
                    )
                }
            }
        }
    }

    private val internetListener = InternetListener(internetListenerCallback)

    private val currentScreenId: Int
        get() = bottomNavigationController.currentScreenId

    override fun onHideKeyboard() {
        binding.navigationView.isVisible = true
    }

    override fun onShowKeyboard() {
        binding.navigationView.isGone = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachKeyboardListeners()
        setContentView(binding.root)
        setupInternetListener()
        setupToolbar()
        setupObservables()
        setupNotifications()
        initPages()
    }

    private fun setupService() {
        startService(OfflineListenerService.createIntent(this))
    }

    private fun setupToolbar() {
        with(binding.toolbarLayout) {
            setToolbar(toolbar, false)
            syncBtn.setOnClickListener {
                viewModel.openSyncScreen()
            }
        }
    }

    private fun setupInternetListener() {
        internetListener.setup(this)
        internetListener.trigger()
        setupService()
    }

    private fun setupObservables() {
        viewModel.pagesLiveData.observe(this) { pages ->
            if (pages.isNullOrEmpty()) return@observe
            bottomNavigationController.setPages(pages)
            bottomNavigationController.init()
        }
        viewModel.isInOfflineLiveData.observe(this) { isInOffline ->
            if (isInOffline.not()) {
                startService(SendPendingActionsService.createIntent(this@CoreActivity))
            }
            setOnlineStatus(isInOffline.not())
        }
        viewModel.roleLiveData.observe(this) { role ->
            when (role) {
                Role.OBSERVER -> {
                    hideOnlineFeatures()
                }
                Role.SECURITY_MANAGER -> {
                    hideOnlineFeatures()
                }
                else -> Unit
            }
        }
        viewModel.savedTypes.observe(this) { offlineSaves ->
            val briefingsSaved = offlineSaves.any { it.typeCode == SaveType.BRIEFINGS.code }
            if (briefingsSaved.not()) {
                bottomNavigationController.setEnabledMenuItem(R.id.page_briefings, false)
            }
        }
    }

    private fun hideOnlineFeatures() {
        binding.toolbarLayout.onlineStatus.isGone = true
        binding.toolbarLayout.syncBtn.isGone = true
    }

    private fun setOnlineStatus(isInOnline: Boolean) {
        with(binding.toolbarLayout) {
            if (isInOnline) {
                notificationsBtn.isVisible = true
                onlineStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    ru.metasharks.catm.core.ui.R.drawable.ic_online,
                    0
                )
                onlineStatus.text = getString(R.string.status_online)
                onlineStatus.setOnClickListener {
                    viewModel.openOfflineSwitcherScreen(
                        fromOnline = true,
                        force = false
                    )
                }
            } else {
                notificationsBtn.isGone = true
                onlineStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    ru.metasharks.catm.core.ui.R.drawable.ic_offline,
                    0
                )
                onlineStatus.text = getString(R.string.status_offline)
                onlineStatus.setOnClickListener {
                    viewModel.openOfflineSwitcherScreen(
                        fromOnline = false,
                        force = false
                    )
                }
            }
        }
    }

    private fun initPages() {
        bottomNavigationController = BottomNavigationController(
            supportFragmentManager,
            binding.navigationView,
            binding.fragmentContainer,
            onPageSelectedListener = this::pageSelected
        )
        viewModel.init()
    }

    private fun pageSelected(coreScreenPage: CoreScreenPage, screenId: Int) {
        hideKeyboard()
        showBackPressed(screenId)
    }

    override fun onBackPressed() {
        if (isKeyboardVisible(binding.root)) {
            hideKeyboard()
            return
        }
        val onBackPressedAction = screensBackPressedActions[currentScreenId]
        if (onBackPressedAction != null) {
            onBackPressedAction.invoke()
            return
        }
        if (!bottomNavigationController.isOnInitialScreen()) {
            bottomNavigationController.returnToInitialScreen()
            return
        }
        viewModel.exit()
    }

    override fun showBack(show: Boolean, onBackPressedAction: (() -> Unit)?) {
        if (onBackPressedAction == null) {
            screensBackPressedActions.remove(currentScreenId)
        } else {
            screensBackPressedActions[currentScreenId] = onBackPressedAction
        }
        showBackPressed(currentScreenId)
    }

    override fun updateNotifications() {
        binding.toolbarLayout.notificationsBtn.refresh()
    }

    private fun showBackPressed(screenId: Int) {
        val show = screensBackPressedActions[screenId] != null
        if (show) {
            binding.toolbarLayout.catmLogo.isGone = true
            requireSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true)
            requireSupportActionBar().setDisplayHomeAsUpEnabled(true)
        } else {
            binding.toolbarLayout.catmLogo.isVisible = true
            requireSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false)
            requireSupportActionBar().setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        detachKeyboardListeners()
        internetListener.clear(this)
        super.onDestroy()
    }

    // test fun
    private fun setupNotifications() {
        startForegroundService(NotificationsService.createStartIntent(this))
    }
}
