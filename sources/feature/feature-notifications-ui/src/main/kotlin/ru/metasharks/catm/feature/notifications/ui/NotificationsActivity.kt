package ru.metasharks.catm.feature.notifications.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.notifications.ui.databinding.ActivityNotificationsBinding
import ru.metasharks.catm.feature.notifications.ui.recycler.NotificationsAdapter
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.ButtonActionItemUi
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.ClickPayload
import ru.metasharks.catm.utils.dpToPx
import javax.inject.Inject

@AndroidEntryPoint
internal class NotificationsActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val binding: ActivityNotificationsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: NotificationsViewModel by viewModels()

    private val notificationsAdapter: NotificationsAdapter by lazy {
        NotificationsAdapter(::onButtonAction, ::onDismiss)
    }

    private fun onButtonAction(item: ButtonActionItemUi) {
        when (val payload = item.payload) {
            is ClickPayload.WorkPermit ->
                viewModel.openWorkPermit(payload.workPermitId)
            is ClickPayload.Briefing ->
                viewModel.openBriefing(payload.briefingId)
            is ClickPayload.CurrentUser ->
                viewModel.openCurrentUserProfile()
            is ClickPayload.User ->
                viewModel.openUserProfile(payload.userId)
        }
        viewModel.readNotification(item.notificationId)
    }

    private fun onDismiss(notificationId: Long) {
        viewModel.readNotification(notificationId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.loadNotifications()
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbar, showTitle = true, title = title.toString())
            notificationRecycler.adapter = notificationsAdapter
            notificationRecycler.addItemDecoration(
                DividerItemDecoration(
                    dpToPx(ITEM_GAP_DP),
                    orientation = DividerItemDecoration.VERTICAL,
                    showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                )
            )
        }
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            errorHandler.handle(binding.root, error)
        }
        viewModel.adapterItems.observe(this) { items ->
            notificationsAdapter.items = items
            binding.clearAllBtn.isGone = items.isEmpty()
            binding.clearAllBtn.setOnClickListener {
                viewModel.readAllNotifications()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val ITEM_GAP_DP = 16

        fun createIntent(context: Context): Intent {
            return Intent(context, NotificationsActivity::class.java)
        }
    }
}
