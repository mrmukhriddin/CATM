package ru.metasharks.catm.core.ui.dialog.pick

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.databinding.ItemPickDialogBinding
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.utils.layoutInflater

class PickItemDelegate(
    private val onClickListener: (PickItemDialog.ItemUi) -> Unit,
) : BaseListItemDelegate<PickItemDialog.ItemUi, PickItemDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemPickDialogBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: PickItemDialog.ItemUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            if (payloads.isNotEmpty()) {
                val isSelected = payloads.first() as? Boolean
                isSelected?.let {
                    checkMark.isVisible = it
                    return
                }
            }
            pickText.text = item.value
            checkMark.isVisible = item.isSelected
            root.setOnClickListener {
                onClickListener(item)
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is PickItemDialog.ItemUi

    class ViewHolder(val binding: ItemPickDialogBinding) : RecyclerView.ViewHolder(binding.root)
}
