package ru.metasharks.catm.feature.workpermit.ui.details.workers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitDetailsWorkersBinding
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.PickWorkerDialog
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler.UserAdapter
import ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler.WorkerMainInfoWrapper
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx

@AndroidEntryPoint
internal class WorkersActivity : BaseActivity(), PickWorkerDialog.Callback {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)
    private val binding: ActivityWorkPermitDetailsWorkersBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WorkersViewModel by viewModels()

    private val workersAdapter: UserAdapter by lazy {
        UserAdapter(
            ::onWorkerChangeClicked,
            viewModel.originalToReplacementUserId.values::toList
        )
    }

    private fun onWorkerChangeClicked(info: WorkerMainInfoWrapper) {
        val userIdsToExclude = viewModel.items.map { it.id }.plus(info.id)
        PickWorkerDialog.newInstance(
            viewModel.newItems,
            excludedWorkersIds = userIdsToExclude,
            changePayload = PickWorkerDialog.ChangePayload(
                changeMode = true,
                replacingWorkerInfo = PickWorkerDialog.ChangePayload.ReplacingWorkerInfo(
                    replacingWorkerUserId = info.userId,
                    replacingWorkerId = info.workerId ?: info.replacingWorkerId,
                )
            )
        ).show(supportFragmentManager, PickWorkerDialog.TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupObservers()
        setupUi()

        viewModel.load(workPermitId)
    }

    private fun setupUi() {
        setToolbar(binding.toolbarContainer.toolbar, showTitle = true, title = title.toString())
        binding.workersRecycler.apply {
            adapter = workersAdapter
            addItemDecoration(
                DividerItemDecoration(
                    dpToPx(SPACE_BETWEEN_DP),
                    orientation = DividerItemDecoration.VERTICAL,
                    showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                )
            )
        }
    }

    private fun setupObservers() {
        viewModel.workPermit.observe(this) { workPermit ->
            if (workPermit.status == StatusCode.SIGNED) {
                setupSigned(workPermit)
            } else {
                setupDefault(workPermit)
            }
        }
        viewModel.adapterItems.observe(this) { newItems ->
            workersAdapter.items = newItems
        }
        viewModel.buttonState.observe(this) { buttonState ->
            setButtonState(buttonState)
        }
        viewModel.workerUserIdToRefresh.observe(this) { workerId ->
            workersAdapter.refreshItemWithWorkerUserId(workerId)
        }
    }

    private fun setButtonState(buttonState: WorkersViewModel.ButtonState) {
        when (buttonState) {
            is WorkersViewModel.ButtonState.Hidden -> {
                binding.pendingActionWarning.isGone = true
                binding.buttonsContainer.isGone = true
            }
            is WorkersViewModel.ButtonState.NewWorkersAdded -> {
                binding.pendingActionWarning.isGone = true
                binding.buttonsContainer.isVisible = true
                binding.primaryButton.isEnabled = true
                binding.primaryButton.setOnClickListener {
                    viewModel.addNewWorkers()
                }
                binding.primaryButton.setText(R.string.btn_save_workers)
                binding.primaryButtonWarning.setText(R.string.btn_save_workers_warning)
            }
            is WorkersViewModel.ButtonState.SignNewWorkers -> {
                binding.pendingActionWarning.isGone = true
                binding.buttonsContainer.isVisible = true
                if (buttonState.isAvailable) {
                    binding.primaryButton.isEnabled = true
                    binding.primaryButton.setOnClickListener {
                        viewModel.signNewWorkers(buttonState.briefingId)
                    }
                } else {
                    binding.primaryButton.isEnabled = false
                    binding.primaryButton.setOnClickListener(null)
                }
                binding.primaryButton.setText(R.string.btn_sign_workers)
                binding.primaryButtonWarning.setText(R.string.btn_sign_workers_warning)
            }
            is WorkersViewModel.ButtonState.PendingAction -> {
                workersAdapter.setIsEnabledReplacement(false)
                binding.addWorkersBtn.isGone = true
                binding.pendingActionWarning.isVisible = true
                binding.buttonsContainer.isVisible = true
                if (buttonState.isPendingAdd) {
                    binding.primaryButton.setText(R.string.btn_save_workers)
                    binding.primaryButton.isEnabled = false
                    binding.primaryButtonWarning.setText(R.string.btn_save_workers_warning)
                } else {
                    binding.primaryButton.setText(R.string.btn_sign_workers)
                    binding.primaryButton.isEnabled = false
                    binding.primaryButtonWarning.setText(R.string.btn_sign_workers_warning)
                }
            }
        }
    }

    private fun setupDefault(workPermit: WorkPermitDetailsUi) {
        with(binding) {
            addWorkersBtn.isGone = true
            buttonsContainer.isGone = true
        }
        workersAdapter.items = workPermit.workers.map { it.user }
    }

    private fun setupSigned(workPermit: WorkPermitDetailsUi) {
        with(binding) {
            addWorkersBtn.isVisible = true
            addWorkersBtn.setOnClickListener {
                PickWorkerDialog.newInstance(
                    viewModel.newItems,
                    excludedWorkersIds = workPermit.workers.map { it.user.id },
                    changePayload = PickWorkerDialog.ChangePayload(changeMode = false)
                ).show(supportFragmentManager, PickWorkerDialog.TAG)
            }
        }
        workersAdapter.items = workPermit.workers
    }

    override fun onItemPick(item: WorkerListItemUi, isSelected: Boolean) {
        viewModel.selectItem(item, isSelected)
    }

    override fun onItemChange(
        item: WorkerListItemUi,
        replacingWorkerInfo: PickWorkerDialog.ChangePayload.ReplacingWorkerInfo
    ) {
        viewModel.replace(item, replacingWorkerInfo)
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val SPACE_BETWEEN_DP = 20

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, WorkersActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
