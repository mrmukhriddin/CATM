package ru.metasharks.catm.feature.createworkpermit.ui

import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.ActivityCreateWorkPermitBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.CreateWorkPermitProcessManager
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepCallback
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.WorkTypeCallback
import ru.metasharks.catm.step.entities.Progress
import ru.metasharks.catm.step.entities.RestoreData

@AndroidEntryPoint
class CreateWorkPermitActivity : BaseActivity(), StepCallback, WorkTypeCallback, ToolbarCallback {

    private val binding: ActivityCreateWorkPermitBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: CreateWorkPermitViewModel by viewModels()
    private var isCreatingWorkPermit: Boolean = false

    private val processManager: CreateWorkPermitProcessManager by lazy {
        CreateWorkPermitProcessManager(
            supportFragmentManager,
            binding.fragmentContainer,
            binding.stepTextDescription,
            this::onProcessEnd,
        )
    }

    private fun onProcessEnd() {
        viewModel.createWorkPermit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupObservers()
        setupUi()

        viewModel.load()
    }

    private fun setupObservers() {
        viewModel.process.observe(this) { process ->
            processManager.init(process)
            processManager.start()
        }
        viewModel.updatePayload.observe(this) { payload ->
            when (payload) {
                is Payload.Forward -> processManager.nextStep()
                is Payload.Back -> processManager.back()
            }
        }
    }

    private fun setupUi() {
        attachKeyboardListeners()
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showTitle = true,
            title = title.toString(),
            showNavigate = true
        )
    }

    override fun onBackPressed() {
        if (isCreatingWorkPermit) {
            return
        }
        if (viewModel.currentProgress == Progress.START || viewModel.currentProgress == Progress.PREPARATION) {
            viewModel.exit()
        } else {
            viewModel.back()
        }
    }

    override fun endStep(output: Any?, restoreData: RestoreData?) {
        viewModel.saveOutput(output, restoreData)
    }

    override fun back() {
        viewModel.back()
    }

    override fun setWorkTypeName(workType: String) {
        processManager.setWorkType(workType)
        requireSupportActionBar().title =
            getString(R.string.work_permit_with_work_type, workType.lowercase())
    }

    override fun showBack(show: Boolean, onBackPressedAction: (() -> Unit)?) {
        if (onBackPressedAction == null) {
            isCreatingWorkPermit = true
        }
    }
}
