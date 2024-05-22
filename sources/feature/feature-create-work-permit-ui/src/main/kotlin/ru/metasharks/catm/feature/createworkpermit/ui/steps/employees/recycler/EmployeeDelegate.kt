package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.RecyclerUtils
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.ItemEmployeeBinding
import ru.metasharks.catm.utils.layoutInflater

class EmployeeDelegate(
    private val onAddActionButtonClick: (EmployeeUi) -> Unit
) : BaseListItemDelegate<EmployeeUi, EmployeeDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemEmployeeBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(item: EmployeeUi, holder: ViewHolder, payloads: List<Any?>) {
        with(holder.binding) {
            val payload = payloads.firstOrNull()
            if (payload is EmployeesAdapter.Payload) {
                addActionBtn.isActivated = payload.isSelected
                return
            }
            RecyclerUtils.makeTransparent(root, item.isReady, addActionBtn)
            addActionBtn.isEnabled = item.isReady
            addActionBtn.setOnClickListener { onAddActionButtonClick(item) }
            profileImageContainer.setData(item.fullName, item.avatar).load()
            profileName.text = "${item.name}\n${item.surname}"
            profilePosition.text = item.position
            isReadyIndicator.isEnabled = item.isReady
            addActionBtn.isActivated = item.isSelected
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is EmployeeUi

    class ViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root)
}
