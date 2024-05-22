package ru.metasharks.catm.core.ui.recycler.pagination

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.DelegateStaticView
import ru.metasharks.catm.core.ui.recycler.ProgressItem
import ru.metasharks.catm.core.ui.recycler.RecyclerViewUpdater

typealias OnNearTheEndListener = () -> Unit

open class PaginationListDelegationAdapter(
    private val onNearTheEndListener: OnNearTheEndListener?
) : ListDelegationAdapter<List<BaseListItem>>() {

    protected open val nextPageLoadingThreshold = ITEMS_COUNT_AT_BOTTOM_TO_START_LOADING_NEXT

    init {
        items = emptyList()
        delegatesManager.addDelegate(
            DelegateStaticView(
                ProgressItem::class.java,
                R.layout.item_loader
            )
        )
    }

    fun setItemsWithoutDiffing(items: List<BaseListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun setItems(items: List<BaseListItem>?) {
        val oldItems = this.items ?: emptyList()
        val newItems = items ?: emptyList()
        this.items = newItems

        if (oldItems.isEmpty()) {
            notifyDataSetChanged()
        } else {
            RecyclerViewUpdater.Builder(oldItems, newItems).apply {
                identityCallback = BaseListItem::areSame
                changePayloadCallback = { _, _ -> }
            }
                .build()
                .update(this)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        tryToFireNearTheEndEvent(position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<*>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        tryToFireNearTheEndEvent(position)
    }

    private fun tryToFireNearTheEndEvent(position: Int) {
        items?.let { currentItems ->
            if (position >= currentItems.size - nextPageLoadingThreshold) {
                onNearTheEndListener?.invoke()
            }
        }
    }

    companion object {
        private const val ITEMS_COUNT_AT_BOTTOM_TO_START_LOADING_NEXT = 10
    }
}
