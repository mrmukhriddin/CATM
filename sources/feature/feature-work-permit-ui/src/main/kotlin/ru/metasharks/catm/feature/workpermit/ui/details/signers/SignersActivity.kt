package ru.metasharks.catm.feature.workpermit.ui.details.signers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.core.ui.view.LabeledWrapper
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitDetailsSignersBinding
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemUserBinding
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.SignerListItemUi
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.setDefiniteMargin
import ru.metasharks.catm.utils.setDefinitePadding

@AndroidEntryPoint
class SignersActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val binding: ActivityWorkPermitDetailsSignersBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WorkPermitFromDbViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId)
    }

    private fun setupUi() {
        setToolbar(binding.toolbarContainer.toolbar, showTitle = true, title = title.toString())
    }

    private fun setupObservers() {
        viewModel.workPermit.observe(this) { workPermit ->
            setSigners(workPermit.signers)
        }
    }

    private fun setSigners(signers: List<SignerListItemUi>) {
        signers.forEach { binding.signersContainer.addView(createSignerItem(it)) }
    }

    private fun createSignerItem(signer: SignerListItemUi): View {
        val user = signer.user
        val labelledWrapper = LabeledWrapper(this)
        labelledWrapper.setDefinitePadding(bottom = dpToPx(LABELLED_WRAPPER_PADDING))
        labelledWrapper.label = signer.roleName
        labelledWrapper.textColor =
            ContextCompat.getColor(this, ru.metasharks.catm.core.ui.R.color.light_gray)
        val itemUserBinding = ItemUserBinding.inflate(layoutInflater, labelledWrapper, false)
        with(itemUserBinding) {
            root.setDefiniteMargin(top = dpToPx(TOP_MARGIN))
            if (user.isReady == null) {
                isReadyIndicator.isEnabled = false
            } else if (user.isReady) {
                isReadyIndicator.isEnabled = true
            } else {
                isReadyIndicator.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SignersActivity,
                        ru.metasharks.catm.core.ui.R.drawable.ic_error
                    )
                )
            }
            with(profileInfo) {
                profileName.text = getFullNameWithLineBreak(user.name, user.surname)
                if (user.position == null) {
                    profilePosition.isGone = true
                } else {
                    profilePosition.isVisible = true
                    profilePosition.text = user.position
                }
                profilePosition.text = user.position
                profileImageContainer.setData(getFullName(user.name, user.surname), user.avatar)
                    .load()
            }
        }
        labelledWrapper.addView(itemUserBinding.root)
        return labelledWrapper
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val LABELLED_WRAPPER_PADDING = 20
        private const val TOP_MARGIN = 12

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, SignersActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
