package ru.metasharks.catm.core.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import ru.metasharks.catm.core.ui.R

abstract class BaseListItemDelegate<T : BaseListItem, VH : RecyclerView.ViewHolder> :
    AdapterDelegate<List<BaseListItem>>() {

    override fun isForViewType(items: List<BaseListItem>, position: Int): Boolean =
        isForViewType(items[position])

    @Suppress("UNCHECKED_CAST")
    public override fun onBindViewHolder(
        items: List<BaseListItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position] as T
        holder.itemView.setTag(R.id.tag_bound_list_item, item)
        onBindViewHolder(item, holder as VH, payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        holder.itemView.setTag(R.id.tag_bound_list_item, null)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getItemFromViewHolder(holder: VH): T {
        return holder.itemView.getTag(R.id.tag_bound_list_item) as T
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup): VH

    protected abstract fun onBindViewHolder(
        item: T,
        holder: VH,
        payloads: List<Any?>
    )

    protected abstract fun isForViewType(item: BaseListItem): Boolean
}
