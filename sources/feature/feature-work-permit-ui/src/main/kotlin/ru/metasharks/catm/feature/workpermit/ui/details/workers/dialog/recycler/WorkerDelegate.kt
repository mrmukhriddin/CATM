package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.RecyclerUtils
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemDialogWorkerBinding
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.utils.layoutInflater

class WorkerDelegate(
    private val onAddActionButtonClick: (WorkerListItemUi) -> Unit,
    private val onChangeActionButtonClick: (WorkerListItemUi) -> Unit,
    private val isChangeModeGetter: () -> Boolean
) : BaseListItemDelegate<WorkerListItemUi, WorkerDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDialogWorkerBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: WorkerListItemUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            val payload = payloads.firstOrNull()
            if (payload is WorkerAdapter.Payload) {
                actionBtn.isActivated = payload.isSelected
                return
            }
            RecyclerUtils.makeTransparent(root, item.isReady, actionBtn)
            actionBtn.isEnabled = item.isReady
            profileImageContainer.setData(item.fullName, item.avatar).load()
            profileName.text = "${item.name}\n${item.surname}"
            profilePosition.text = item.position
            isReadyIndicator.isEnabled = item.isReady
            actionBtn.isActivated = item.isSelected
            if (isChangeModeGetter()) {
                actionBtn.setImageResource(ru.metasharks.catm.core.ui.R.drawable.ic_replace_btn)
                actionBtn.setOnClickListener { onChangeActionButtonClick(item) }
            } else {
                actionBtn.setOnClickListener { onAddActionButtonClick(item) }
                actionBtn.setImageResource(ru.metasharks.catm.core.ui.R.drawable.ic_add_button_state)
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkerListItemUi

    class ViewHolder(val binding: ItemDialogWorkerBinding) : RecyclerView.ViewHolder(binding.root)
}
