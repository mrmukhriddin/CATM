package ru.metasharks.catm.feature.workpermit.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.chips.ChipUtils
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.dialog.confirmation.OnButtonClick
import ru.metasharks.catm.core.ui.view.LoadingMaterialButtonWrapper
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.ui.MenuEntry
import ru.metasharks.catm.feature.workpermit.ui.MenuManager
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.StatusArchivedMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.StatusNewMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.StatusPendingMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.StatusSignedMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitDetailsBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.archived.ArchivedAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.pending.PendingAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.strings.getSizeString

@Suppress("LargeClass")
@AndroidEntryPoint
class WorkPermitDetailsActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val binding: ActivityWorkPermitDetailsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WorkPermitDetailsViewModel by viewModels()

    private val menuManager: MenuManager by lazy { MenuManager(this, binding.menuOptionsContainer) }
    private val views: WorkPermitDetailsViewCreator by lazy { WorkPermitDetailsViewCreator() }

    private val WorkPermitDetailsUi.workersPassed: Boolean
        get() = workersSignedCount == workersCount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupBounds()
        setupObservers()
        setupToolbar()

        viewModel.init(workPermitId)
    }

    private fun setupBounds() {
        views.init(this, binding.buttonsContainer, viewModel)
    }

    private fun setupToolbar() {
        setToolbar(binding.toolbar, showTitle = false, showNavigate = true)
    }

    private fun setupUi() {
        with(binding) {
            documentContainer.iconFile.isGone = true
            workPermitTitle.text = getString(R.string.wp_item_work_permit_number, workPermitId)
            contentContainer.setOnRefreshListener {
                viewModel.refresh(workPermitId)
            }
        }
    }

    private fun setupObservers() {
        viewModel.updatePayload.observe(this) { payload ->
            if (payload is WorkPermitDetailsViewModel.OfflinePayload) {
                processOfflinePayload(payload)
            } else {
                processUpdatePayload(payload)
            }
        }
        viewModel.updatedRole.observe(this) { (signer, isAllowed) ->
            if (binding.secondaryButton.isGone) {
                with(binding.secondaryButton) {
                    if (isAllowed) {
                        isVisible = true
                        setText(R.string.btn_reject_text)
                        setOnClickListener { showRejectionDialog(signer) }
                    } else if (viewModel.workPermit.isCreator) {
                        isVisible = true
                        setText(R.string.btn_delete_text)
                        setOnClickListener { showDeletionDialog() }
                    }
                }
            } else {
                if (isAllowed) {
                    with(binding.secondaryButton) {
                        isVisible = true
                        setText(R.string.btn_reject_text)
                        setOnClickListener { showRejectionDialog(signer) }
                    }
                }
            }
            if (isAllowed) {
                val view = binding.buttonsContainer.findViewWithTag<View>(signer.code)
                view?.isEnabled = true
            }
        }
    }

    private fun processUpdatePayload(payload: WorkPermitDetailsViewModel.Payload) {
        when (payload) {
            is WorkPermitDetailsViewModel.Payload.LoadedWorkPermit -> setWorkPermit(payload.workPermit)
            is WorkPermitDetailsViewModel.Payload.Count -> processCountPayload(payload)
            is WorkPermitDetailsViewModel.Payload.SuccessfulSigning -> processSuccessfulSigningPayload(
                payload
            )
            is WorkPermitDetailsViewModel.Payload.Reject -> processRejectPayload(payload)
            is WorkPermitDetailsViewModel.Payload.StopRefreshIndicator ->
                binding.contentContainer.isRefreshing = false
            is WorkPermitDetailsViewModel.OfflinePayload -> return
        }
    }

    private fun processOfflinePayload(payload: WorkPermitDetailsViewModel.OfflinePayload) {
        when (payload) {
            is WorkPermitDetailsViewModel.OfflinePayload.DeletePending -> {
                binding.secondaryButton.isLoading = false
                binding.primaryButton.isLoading = false
                binding.secondaryButton.isEnabled = false
                binding.primaryButton.isEnabled = false
                binding.offlinePendingActionWarning.isVisible = true
            }
            is WorkPermitDetailsViewModel.OfflinePayload.OfflineMode -> {
                binding.contentContainer.isEnabled = false
                binding.contentContainer.isRefreshing = false
            }
        }
    }

    private fun setWorkPermit(workPermit: WorkPermitDetailsUi) {
        clearScreen()
        clearButtons()
        initCommonInfo(workPermit)
        initDocument(workPermit.document)
        initButtons(workPermit)
        initMenuOptions(workPermit)
    }

    private fun processCountPayload(payload: WorkPermitDetailsViewModel.Payload.Count) {
        menuManager.updateMenu(payload.menuOptionTag) { binding ->
            if (payload.overall == null) {
                binding.count.text = payload.count.toString()
            } else {
                binding.count.text = getString(
                    R.string.pattern_number_of_overall,
                    payload.count,
                    payload.overall
                )
            }
        }
    }

    private fun processSuccessfulSigningPayload(payload: WorkPermitDetailsViewModel.Payload.SuccessfulSigning) {
        val button =
            binding.buttonsContainer.findViewWithTag<LoadingMaterialButtonWrapper>(payload.role.code)
                ?: return
        val posToInsert = binding.buttonsContainer.indexOfChild(button)
        val signedView = views.createSignedView()
        binding.buttonsContainer.removeViewAt(posToInsert)
        binding.buttonsContainer.addView(signedView, posToInsert)
        menuManager.updateMenu(StatusPendingMenuEntry.MENU_OPTION_SIGNERS) { binding ->
            binding.count.text = getString(
                R.string.pattern_number_of_overall,
                payload.workPermit.signersSignedCount,
                payload.workPermit.signersCount
            )
        }
        if (payload.workPermit.isRoleSigned(SignerRole.PERMITTER, true)) {
            menuManager.updateMenu(StatusPendingMenuEntry.MENU_OPTION_FACTORS_BRIEFING) {
                it.check.isEnabled = true
            }
        }
        checkRiskFactorsHide(payload.workPermit)
        checkIfHideRejectButton(payload.workPermit)
    }

    private fun processRejectPayload(payload: WorkPermitDetailsViewModel.Payload.Reject) {
        clearButtons()
        val workPermit = payload.workPermit
        with(binding) {
            if (workPermit.isCreator) {
                with(secondaryButton) {
                    isLoading = false
                    isVisible = true
                    setText(R.string.btn_delete_text)
                    setOnClickListener { showDeletionDialog() }
                }
            }
            buttonsContainer.addView(views.createRejectedView(), 0)
        }
    }

    private fun checkIfHideRejectButton(workPermit: WorkPermitDetailsUi) {
        val signerAvailable = viewModel.availableSignings.firstOrNull()
        if (signerAvailable != null) {
            with(binding.secondaryButton) {
                isVisible = true
                isLoading = false
                setText(R.string.btn_reject_text)
                setOnClickListener { showRejectionDialog(signerAvailable) }
            }
        } else if (workPermit.isCreator) {
            with(binding.secondaryButton) {
                isVisible = true
                isLoading = false
                setText(R.string.btn_delete_text)
                setOnClickListener { showDeletionDialog() }
            }
        } else {
            binding.secondaryButton.isGone = true
        }
    }

    private fun clearButtons() {
        with(binding) {
            buttonsContainer.children
                .filter { it.id == View.NO_ID }
                .map { buttonsContainer.children.indexOf(it) }
                .sortedByDescending { it }
                .forEach {
                    buttonsContainer.removeViewAt(it)
                }
            primaryButton.isGone = true
            secondaryButton.isGone = true
        }
    }

    private fun initCommonInfo(workPermit: WorkPermitDetailsUi) {
        val main = workPermit.mainInfo
        with(binding) {
            ChipUtils.addChip(statusChipContainer, main.chips.first())
            ChipUtils.addChip(workTypeChipContainer, main.workTypeChip)
            createdDate.text =
                getString(R.string.wp_item_created, LocalDateUtils.toString(main.createdDate))
            place.text = main.address
            workersCount.text = getString(R.string.wp_item_workers_count, workPermit.workersCount)
            offlinePendingActionWarning.isGone = workPermit.anyOfflineActionEnabled()
        }
    }

    private fun initDocument(document: DocumentUi?) {
        with(binding) {
            with(documentContainer) {
                if (document == null) {
                    root.isGone = true
                } else {
                    val sizeString = getSizeString(document.fileSizeBytes)
                    root.isVisible = true
                    iconFile.isVisible = true
                    fileName.text = document.fileName
                    fileTypeAndSize.text = sizeString
                    root.setOnClickListener {
                        viewModel.openPdfFile(document.fileUrl)
                    }
                }
            }
        }
    }

    private fun initButtonsNew(workPermit: WorkPermitDetailsUi) {
        with(binding) {
            checkRiskFactorsBtn.isGone = true
            if (workPermit.isCreator) {
                with(primaryButton) {
                    isVisible = true
                    setText(R.string.btn_send_to_signing_text)
                    isEnabled = workPermit.workersPassed && workPermit.anyOfflineActionEnabled()
                    setOnClickListener { showSendSigningDialog() }
                }
                with(secondaryButton) {
                    isEnabled = workPermit.anyOfflineActionEnabled()
                    isVisible = true
                    setText(R.string.btn_delete_text)
                    setOnClickListener { showDeletionDialog() }
                }
            }
        }
    }

    private fun initButtonsPending(workPermit: WorkPermitDetailsUi) {
        with(binding) {
            val additionalInfo = workPermit.additionalInfo as PendingAdditionalInfo
            val isSigner = additionalInfo.rolesList.any { (_, signed) -> signed == null }
            with(secondaryButton) {
                if ((isSigner.not() || additionalInfo.anyRejected) && workPermit.isCreator) {
                    isEnabled = workPermit.anyOfflineActionEnabled()
                    isVisible = true
                    text = getString(R.string.btn_delete_text)
                    setOnClickListener { showDeletionDialog() }
                }
            }
            checkRiskFactorsHide(workPermit)
            if (additionalInfo.anyRejected) {
                buttonsContainer.addView(views.createRejectedView(), 0)
            } else {
                with(binding.buttonsContainer) {
                    additionalInfo.rolesList.forEachIndexed { i, (signer, signed) ->
                        if (signed == null) {
                            addView(
                                views.createSignButton(signer),
                                0
                            )
                        } else if (signed == true) {
                            addView(
                                views.createSignedView(),
                                i
                            )
                        }
                    }
                }
                viewModel.checkEnabledRoles(additionalInfo.rolesList
                    .filter { it.second == null }
                    .map { it.first })
            }
        }
    }

    private fun initButtonsSigned(workPermit: WorkPermitDetailsUi) {
        val additionalInfo = workPermit.additionalInfo as SignedAdditionalInfo
        val extension = additionalInfo.extension
        with(binding) {
            if (extension != null && extension.isSigner) {
                if (extension.isAwaiting) {
                    buttonsContainer.addView(
                        views.createSignExtensionButton(
                            extension,
                            additionalInfo.offlinePendingSignExtension
                        )
                    )
                } else {
                    buttonsContainer.addView(views.createSignedView())
                }
            }
            if (workPermit.isCreator) {
                if (extension == null) {
                    buttonsContainer.addView(views.createExtendButton(additionalInfo.offlinePendingExtend))
                } else {
                    buttonsContainer.addView(views.createAwaitingSignExtensionButton(extension))
                }
                buttonsContainer.addView(views.createCloseButton(additionalInfo.offlinePendingClose))
            }
        }
    }

    private fun checkRiskFactorsHide(workPermit: WorkPermitDetailsUi) {
        with(binding) {
            if (workPermit.isPermitter && workPermit.isRoleSigned(SignerRole.PERMITTER, null)) {
                checkRiskFactorsWarning.isVisible = true
                with(checkRiskFactorsBtn) {
                    isVisible = true
                    setOnClickListener { viewModel.openRiskFactorsScreen() }
                }
            } else {
                checkRiskFactorsWarning.isGone = true
                checkRiskFactorsBtn.isGone = true
            }
        }
    }

    private fun initButtons(workPermit: WorkPermitDetailsUi) {
        when (workPermit.status) {
            StatusCode.NEW -> initButtonsNew(workPermit)
            StatusCode.PENDING -> initButtonsPending(workPermit)
            StatusCode.SIGNED -> initButtonsSigned(workPermit)
            StatusCode.ARCHIVED -> Unit
        }
    }

    private fun clearScreen() {
        with(binding) {
            statusChipContainer.removeAllViews()
            workTypeChipContainer.removeAllViews()
            menuManager.removeAllViews()
        }
    }

    private fun initMenuOptions(workPermit: WorkPermitDetailsUi) {
        val entry: MenuEntry = when (workPermit.status) {
            StatusCode.NEW -> createNewMenuEntry(workPermit)
            StatusCode.PENDING -> createPendingMenuEntry(workPermit)
            StatusCode.SIGNED -> createSignedMenuEntry(workPermit)
            StatusCode.ARCHIVED -> createArchivedMenuEntry(workPermit)
            else -> return
        }
        menuManager.setMenuEntry(entry)
        menuManager.init()
    }

    private fun createArchivedMenuEntry(workPermit: WorkPermitDetailsUi): MenuEntry {
        val additionalInfo = workPermit.additionalInfo as ArchivedAdditionalInfo
        return StatusArchivedMenuEntry(
            context = this,
            workersCount = workPermit.workersSignedCount to workPermit.workersCount,
            signersCount = workPermit.signersSignedCount to workPermit.signersCount,
            additionalDocumentsCount = workPermit.additionalDocumentsCount,
            gasAnalysisCount = additionalInfo.gasAnalysisList.size,
            dailyPermitsCount = additionalInfo.dailyPermitsList.size,
            error = null
        )
    }

    private fun createNewMenuEntry(workPermit: WorkPermitDetailsUi): StatusNewMenuEntry {
        return StatusNewMenuEntry(
            context = this,
            workersCount = workPermit.workersSignedCount to workPermit.workersCount,
            signersCount = workPermit.signersSignedCount to workPermit.signersCount,
            additionalDocumentsCount = workPermit.additionalDocumentsCount,
            error = if (workPermit.workersPassed.not()) {
                getString(R.string.error_workers_did_not_pass)
            } else {
                null
            }
        ).addOnMenuOptionClickListener(StatusNewMenuEntry.MENU_OPTION_WORKERS) {
            viewModel.openWorkersScreen()
        }.addOnMenuOptionClickListener(StatusNewMenuEntry.MENU_OPTION_SIGNERS) {
            viewModel.openSignersScreen()
        }.addOnMenuOptionClickListener(StatusNewMenuEntry.MENU_OPTION_ADD_DOCUMENTS) {
            viewModel.openAdditionalDocumentsScreen()
        }
    }

    private fun createSignedMenuEntry(workPermit: WorkPermitDetailsUi): StatusSignedMenuEntry {
        val additionalInfo = workPermit.additionalInfo as SignedAdditionalInfo
        return StatusSignedMenuEntry(
            context = this,
            workersCount = workPermit.workersSignedCount to workPermit.workersCount,
            signersCount = workPermit.signersSignedCount to workPermit.signersCount,
            additionalDocumentsCount = workPermit.additionalDocumentsCount,
            briefingPassed = workPermit.isRoleSigned(SignerRole.PERMITTER, true),
            gasAnalysisCount = additionalInfo.gasAnalysisList.size,
            dailyPermitsCount = additionalInfo.dailyPermitsList.size,
            error = null
        ).addOnMenuOptionClickListener(StatusSignedMenuEntry.MENU_OPTION_GAS_ANALYSIS) {
            viewModel.openGasAirAnalysisScreen()
        }.addOnMenuOptionClickListener(StatusSignedMenuEntry.MENU_OPTION_DAILY_PERMITS) {
            viewModel.openDailyPermitsScreen()
        }.addOnMenuOptionClickListener(StatusSignedMenuEntry.MENU_OPTION_ADD_DOCUMENTS) {
            viewModel.openAdditionalDocumentsScreen()
        }.addOnMenuOptionClickListener(StatusSignedMenuEntry.MENU_OPTION_WORKERS) {
            viewModel.openWorkersScreen()
        }.addOnMenuOptionClickListener(StatusSignedMenuEntry.MENU_OPTION_SIGNERS) {
            viewModel.openSignersScreen()
        }
    }

    private fun createPendingMenuEntry(workPermit: WorkPermitDetailsUi): StatusPendingMenuEntry {
        return StatusPendingMenuEntry(
            context = this,
            workersCount = workPermit.workersSignedCount to workPermit.workersCount,
            signersCount = workPermit.signersSignedCount to workPermit.signersCount,
            additionalDocumentsCount = workPermit.additionalDocumentsCount,
            briefingPassed = workPermit.isRoleSigned(SignerRole.PERMITTER, true),
            error = null
        ).addOnMenuOptionClickListener(StatusPendingMenuEntry.MENU_OPTION_WORKERS) {
            viewModel.openWorkersScreen()
        }.addOnMenuOptionClickListener(StatusPendingMenuEntry.MENU_OPTION_SIGNERS) {
            viewModel.openSignersScreen()
        }.addOnMenuOptionClickListener(StatusPendingMenuEntry.MENU_OPTION_ADD_DOCUMENTS) {
            viewModel.openAdditionalDocumentsScreen()
        }
    }

    private fun LoadingMaterialButtonWrapper.showDeletionDialog() {
        showDialog(
            R.string.dialog_delete_wp_title,
            R.string.dialog_btn_negative,
            R.string.dialog_btn_delete_positive,
            R.string.dialog_delete_wp_message,
        ) {
            isLoading = true
            viewModel.deleteWorkPermit()
        }
    }

    private fun LoadingMaterialButtonWrapper.showSendSigningDialog() {
        showDialog(
            R.string.dialog_send_to_signing_title,
            R.string.dialog_btn_negative,
            R.string.dialog_sign_positive,
            R.string.dialog_sign_message,
        ) {
            isLoading = true
            viewModel.sendWorkPermitToSigning()
        }
    }

    private fun LoadingMaterialButtonWrapper.showRejectionDialog(signer: SignerRole) {
        showDialog(
            R.string.dialog_reject_title,
            R.string.dialog_btn_negative,
            R.string.dialog_reject_positive,
            R.string.dialog_reject_message,
        ) {
            isLoading = true
            viewModel.rejectWorkPermit(signer)
        }
    }

    private fun showDialog(
        @StringRes titleRes: Int,
        @StringRes negativeBtnTextRes: Int,
        @StringRes positiveBtnTextRes: Int,
        @StringRes messageRes: Int? = null,
        onButtonClick: OnButtonClick? = null
    ) {
        val builder = ConfirmationDialogBuilder(this@WorkPermitDetailsActivity)
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

    override fun onDestroy() {
        menuManager.onDestroy()
        views.clear()
        super.onDestroy()
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        const val TAG_REJECTED = "rejected"

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, WorkPermitDetailsActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}

private fun LoadingMaterialButtonWrapper.checkIfEnabled(workPermit: WorkPermitDetailsUi): LoadingMaterialButtonWrapper {
    val info = workPermit.additionalInfo as? SignedAdditionalInfo? ?: return this
    isEnabled = info.offlinePendingExtend
    return this
}
