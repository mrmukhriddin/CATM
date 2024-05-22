package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemDailyPermitBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.utils.FeatureToggle
import ru.metasharks.catm.utils.layoutInflater

typealias OnDailyPermitAction = (DailyPermitUi) -> Unit

class DailyPermitDelegate(
    private val onDeleteClick: OnDailyPermitAction,
    private val onEndAndSignClick: OnDailyPermitAction,
    private val onSignClick: OnDailyPermitAction,
) :
    BaseListItemDelegate<DailyPermitUi, DailyPermitDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDailyPermitBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(item: DailyPermitUi, holder: ViewHolder, payloads: List<Any?>) {
        val context = holder.binding.root.context
        with(holder.binding) {
            dpDate.text = item.dateStart
            dpTime.text = getHighlightedString(
                context,
                R.string.dp_time,
                item.timeStart
            )
            dpResponsible.text = getHighlightedString(
                context,
                R.string.dp_responsible,
                item.responsibleSigner.user.getFullName()
            )
            dpPermitter.text = getHighlightedString(
                context,
                R.string.dp_permitter,
                item.permitterSigner.user.getFullName()
            )
            val isSignedByPermitter = item.permitterSigner.signed == true
            val isSignedByResponsible = item.responsibleSigner.signed == true

            when {
                isSignedByResponsible -> setSignedByResponsible(item)
                isSignedByPermitter.not() -> setNotSignedByPermitter(item)
                isSignedByPermitter && item.isCreator -> setSignedByPermitterAndIsCreator(item)
                isSignedByPermitter -> setSignedByPermitter(item)
                else -> setVisibility()
            }
            waitingConnectionWarning.isVisible = item.pendingActionSent
        }
    }

    private fun ItemDailyPermitBinding.setSignedByResponsible(item: DailyPermitUi) {
        checkNotNull(item.dateEnd)
        checkNotNull(item.timeEnd)
        setVisibility(statusDisplayerVisible = true)
        statusDisplayer.text = root.context.getString(
            R.string.btn_dp_work_ended_pattern,
            item.dateEnd,
            item.timeEnd
        )
    }

    private fun ItemDailyPermitBinding.setNotSignedByPermitter(item: DailyPermitUi) {
        setVisibility()
        if (item.isCreator) {
            secondaryButton.isVisible = true
            secondaryButton.isEnabled = item.pendingActionSent.not()
            secondaryButton.setText(R.string.btn_delete_text)
            secondaryButton.setOnClickListener { onDeleteClick(item) }
        } else {
            secondaryButton.isGone = true
            secondaryButton.setOnClickListener(null)
        }
        if (item.isSigner) {
            primaryButton.isVisible = true
            statusDisplayer.isGone = true
            primaryButton.setText(R.string.btn_dp_sign)
            primaryButton.setOnClickListener { onSignClick(item) }
            primaryButton.isEnabled =
                (FeatureToggle.SIGN_DAILY_PERMIT.isEnabled || item.isOffline.not()) &&
                        item.pendingActionSent.not()
        } else {
            primaryButton.isGone = true
            statusDisplayer.isVisible = true
            statusDisplayer.setText(R.string.btn_dp_on_signing)
            primaryButton.setOnClickListener(null)
        }
    }

    private fun ItemDailyPermitBinding.setSignedByPermitterAndIsCreator(item: DailyPermitUi) {
        setVisibility(primaryVisible = true)
        primaryButton.setText(R.string.btn_dp_end_work_and_sign)
        primaryButton.setOnClickListener { onEndAndSignClick(item) }
        primaryButton.isEnabled = item.pendingActionSent.not()
    }

    private fun ItemDailyPermitBinding.setSignedByPermitter(item: DailyPermitUi) {
        setVisibility(statusDisplayerVisible = true)
        statusDisplayer.setText(R.string.btn_dp_signed)
    }

    private fun ItemDailyPermitBinding.setVisibility(
        primaryVisible: Boolean = false,
        secondaryVisible: Boolean = false,
        statusDisplayerVisible: Boolean = false
    ) {
        primaryButton.isVisible = primaryVisible
        secondaryButton.isVisible = secondaryVisible
        statusDisplayer.isVisible = statusDisplayerVisible
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is DailyPermitUi

    class ViewHolder(val binding: ItemDailyPermitBinding) : RecyclerView.ViewHolder(binding.root)
}
