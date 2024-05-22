package ru.metasharks.catm.feature.workpermit.ui.details

import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.view.LoadingMaterialButtonWrapper
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ButtonPrimaryBinding
import ru.metasharks.catm.feature.workpermit.ui.databinding.LayoutRejectedBinding
import ru.metasharks.catm.feature.workpermit.ui.databinding.LayoutSignedBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.ExtensionUi
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

internal class WorkPermitDetailsViewCreator : ActivityBound<WorkPermitDetailsActivity>() {

    private var _buttonsContainer: LinearLayout? = null
    private val buttonsContainer: LinearLayout
        get() = requireNotNull(_buttonsContainer)

    private var _viewModel: WorkPermitDetailsViewModel? = null
    private val viewModel: WorkPermitDetailsViewModel
        get() = requireNotNull(_viewModel)

    fun init(
        activity: WorkPermitDetailsActivity,
        buttonsContainer: LinearLayout,
        viewModel: WorkPermitDetailsViewModel,
    ) {
        super.init(activity)
        this._buttonsContainer = buttonsContainer
        this._viewModel = viewModel
    }

    override fun clear() {
        super.clear()
        this._buttonsContainer = null
    }

    fun createRejectedView(): View {
        val signedViewBinding =
            LayoutRejectedBinding.inflate(activity.layoutInflater, buttonsContainer, false)
        signedViewBinding.root.tag = WorkPermitDetailsActivity.TAG_REJECTED
        return signedViewBinding.root
    }

    fun createSignedView(workPermit: WorkPermitDetailsUi, signer: SignerRole): View {
        val signedViewBinding =
            LayoutSignedBinding.inflate(activity.layoutInflater, buttonsContainer, false)
        with(signedViewBinding) {
            root.text = activity.getString(
                R.string.btn_signed_pattern_text,
                workPermit.getSignerName(signer)
            )
            root.tag = signer.code
        }
        return signedViewBinding.root
    }

    fun createSignedView(): View {
        val signedViewBinding =
            LayoutSignedBinding.inflate(activity.layoutInflater, buttonsContainer, false)
        return signedViewBinding.root
    }

    /**
     * Создание кнопки подписать с именем роли
     */
    fun createSignButton(
        workPermit: WorkPermitDetailsUi,
        signer: SignerRole,
    ): LoadingMaterialButtonWrapper {
        val buttonBinding = createButtonBinding(outlined = false)
        with(buttonBinding) {
            root.isEnabled = false
            root.text = activity.getString(
                R.string.btn_sign_pattern_text,
                workPermit.getSignerName(signer)
            )
            root.setText(R.string.btn_sign_text)
            root.setOnClickListenerAndLoad {
                viewModel.signWorkPermit(signer)
            }
            root.tag = signer.code
        }
        return buttonBinding.root
    }

    /**
     * Создание кнопки подписать без именем роли
     */
    fun createSignButton(
        signer: SignerRole
    ): LoadingMaterialButtonWrapper {
        val buttonBinding = createButtonBinding(outlined = false)
        with(buttonBinding) {
            root.isEnabled = false
            root.setText(R.string.btn_sign_text)
            root.setOnClickListener {
                showDialog(
                    titleRes = R.string.dialog_sign_title,
                    messageRes = R.string.dialog_sign_message,
                    positiveBtnTextRes = R.string.dialog_sign_positive,
                    negativeBtnTextRes = R.string.dialog_btn_cancel,
                    onButtonClick = {
                        viewModel.signWorkPermit(signer)
                        root.isLoading = true
                    }
                )
            }
            root.tag = signer.code
        }
        return buttonBinding.root
    }

    fun createAwaitingSignExtensionButton(extension: ExtensionUi): LoadingMaterialButtonWrapper {
        return createActionButton(
            text = StringResWrapper(
                activity.getString(
                    if (extension.isAwaiting) {
                        R.string.btn_awaiting_extension_text
                    } else {
                        R.string.btn_extended_text
                    },
                    extension.dateEnd
                )
            ),
            outlined = false,
            tag = null,
            isStatusButton = true,
        )
    }

    fun createExtendButton(offlinePendingExtend: Boolean): LoadingMaterialButtonWrapper {
        return createActionButton(text = StringResWrapper(R.string.btn_extend_text),
            outlined = true,
            tag = null,
            action = { viewModel.openExtendWorkPermitScreen() }
        ).apply {
            isEnabled = offlinePendingExtend.not()
        }
    }

    fun createSignExtensionButton(
        extension: ExtensionUi,
        offlinePendingSignExtension: Boolean
    ): LoadingMaterialButtonWrapper {
        return createActionButton(
            text = StringResWrapper(
                activity.getString(
                    R.string.btn_extend_until_text,
                    extension.dateEnd
                )
            ),
            outlined = false,
            tag = null,
            action = ::showSignExtensionWorkPermitDialog
        ).apply {
            isEnabled = offlinePendingSignExtension.not()
        }
    }

    fun createCloseButton(offlinePendingClose: Boolean): LoadingMaterialButtonWrapper {
        return createActionButton(
            text = StringResWrapper(R.string.btn_close_text),
            outlined = false,
            tag = null,
            action = ::showCloseWorkPermitDialog
        ).apply {
            isEnabled = offlinePendingClose.not()
        }
    }

    private fun createActionButton(
        text: StringResWrapper,
        outlined: Boolean,
        action: ((LoadingMaterialButtonWrapper) -> Unit)? = null,
        tag: String? = null,
        isStatusButton: Boolean = false
    ): LoadingMaterialButtonWrapper {
        val buttonBinding = createButtonBinding(outlined)
        with(buttonBinding) {
            root.text = activity.getString(text)
            action?.let {
                root.setOnClickListener { it(root) }
            }
            if (isStatusButton) {
                root.isEnabled = false
                root.wrappedButton.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            ru.metasharks.catm.core.ui.R.color.dark_gray
                        )
                    )
                    background.setTint(
                        ContextCompat.getColor(
                            context,
                            android.R.color.transparent
                        )
                    )
                }
            }
            root.tag = tag
        }
        return buttonBinding.root
    }

    private fun createButtonBinding(outlined: Boolean): ButtonPrimaryBinding {
        val buttonBinding =
            ButtonPrimaryBinding.inflate(activity.layoutInflater, buttonsContainer, false)
        with(buttonBinding.root.wrappedButton) {
            if (outlined) {
                background.setTint(ContextCompat.getColor(context, android.R.color.transparent))
                setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        ru.metasharks.catm.core.ui.R.color.blue_enabled
                    )
                )
                if (this is MaterialButton) {
                    setStrokeColorResource(ru.metasharks.catm.core.ui.R.color.blue_enabled)
                    strokeWidth = activity.dpToPx(1)
                }
            }
            return buttonBinding
        }
    }

    private fun showCloseWorkPermitDialog(buttonWrapper: LoadingMaterialButtonWrapper) {
        ConfirmationDialogBuilder(activity)
            .setTitle(R.string.dialog_title_close_work_permit)
            .setPositiveButtonText(R.string.dialog_btn_close_work_permit)
            .setNegativeButtonText(R.string.dialog_btn_negative)
            .setOnPositiveButtonAction {
                viewModel.closeWorkPermit()
                buttonWrapper.isLoading = true
            }
            .show()
    }

    private fun showSignExtensionWorkPermitDialog(buttonWrapper: LoadingMaterialButtonWrapper) {
        ConfirmationDialogBuilder(activity)
            .setTitle(R.string.dialog_title_sign_extension_work_permit)
            .setPositiveButtonText(R.string.dialog_btn_sign_extension_work_permit)
            .setNegativeButtonText(R.string.dialog_btn_negative)
            .setOnPositiveButtonAction {
                viewModel.signExtension()
                buttonWrapper.isLoading = true
            }
            .show()
    }

    private fun WorkPermitDetailsUi.getSignerName(signerRole: SignerRole): String {
        return signers.first { it.role == signerRole.code }.roleName
    }
}
