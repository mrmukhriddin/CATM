package ru.metasharks.catm.feature.offline.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CoreScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.feature.offline.ui.databinding.ActivityOfflineBinding
import ru.metasharks.catm.utils.argumentDelegate
import javax.inject.Inject

@AndroidEntryPoint
class OfflineSwitcherActivity : BaseActivity() {

    @Inject
    lateinit var offlineModeProvider: OfflineModeProvider

    @Inject
    lateinit var appRouter: ApplicationRouter

    @Inject
    lateinit var coreScreen: CoreScreen

    private val fromOnline: Boolean by argumentDelegate(EXTRA_FROM_ONLINE)
    private val force: Boolean by argumentDelegate(EXTRA_FORCE)

    private val binding: ActivityOfflineBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            if (fromOnline) {
                startDialogOnlineToOffline(force)
            } else {
                startDialogOfflineToOnline(force)
            }
        }
    }

    private fun startDialogOnlineToOffline(force: Boolean) {
        ConfirmationDialogBuilder(this)
            .setTitle(
                if (force) {
                    R.string.dialog_title_no_internet_connection
                } else {
                    R.string.dialog_title_own_will_to_offline
                }
            )
            .setTitleTextSize(TITLE_SIZE_SP)
            .setPositiveButtonText(R.string.dialog_btn_no_internet_connection)
            .setNegativeButtonText(R.string.dialog_btn_cancel)
            .setOnPositiveButtonAction {
                processSwitchToOffline()
            }
            .setOnNegativeButtonAction {
                appRouter.exit()
            }
            .show()
    }

    private fun startDialogOfflineToOnline(force: Boolean) {
        ConfirmationDialogBuilder(this)
            .setTitle(
                if (force) {
                    R.string.dialog_title_has_internet_connection
                } else {
                    R.string.dialog_title_own_will_to_online
                }
            )
            .setTitleTextSize(TITLE_SIZE_SP)
            .setPositiveButtonText(R.string.dialog_btn_has_internet_connection)
            .setNegativeButtonText(R.string.dialog_btn_cancel)
            .setOnPositiveButtonAction {
                processSwitchToOnline()
            }
            .setOnNegativeButtonAction {
                appRouter.exit()
            }
            .show()
    }

    private fun processSwitchToOnline() {
        offlineModeProvider.isInOfflineMode = false
        appRouter.newRootScreen(coreScreen())
    }

    private fun processSwitchToOffline() {
        offlineModeProvider.isInOfflineMode = true
        appRouter.newRootScreen(coreScreen())
    }

    companion object {

        private const val TITLE_SIZE_SP = 16f

        private const val EXTRA_FROM_ONLINE = "from_online"
        private const val EXTRA_FORCE = "force"

        fun createIntent(context: Context, fromOnline: Boolean, force: Boolean = true): Intent {
            return Intent(context, OfflineSwitcherActivity::class.java)
                .putExtra(EXTRA_FROM_ONLINE, fromOnline)
                .putExtra(EXTRA_FORCE, force)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
