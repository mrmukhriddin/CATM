package ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.ViewNumberedItemBinding
import ru.metasharks.catm.utils.layoutInflater

internal typealias OnNumberedItemClickListener = (NumberedItemUi) -> Unit

internal class NumberedItemDelegate(
    private val onItemClickListener: OnNumberedItemClickListener
) :
    BaseListItemDelegate<NumberedItemUi, NumberedItemDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ViewNumberedItemBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(item: NumberedItemUi, holder: ViewHolder, payloads: List<Any?>) {
        with(holder.binding) {
            numberOnList.text = item.numberOnList.toString()
            description.text = item.description
            title.text = item.title
            root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is NumberedItemUi

    class ViewHolder(val binding: ViewNumberedItemBinding) : RecyclerView.ViewHolder(binding.root)
}
