package ru.metasharks.catm.feature.briefings.ui.main.briefings

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi

class BriefingItemsAdapter(
    onClick: (BriefingUi) -> Unit
) : ListDelegationAdapter<List<BaseListItem>>() {

    init {
        delegatesManager.addDelegate(BriefingItemDelegate(onClick))
    }

    fun setBriefings(items: List<BaseListItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
