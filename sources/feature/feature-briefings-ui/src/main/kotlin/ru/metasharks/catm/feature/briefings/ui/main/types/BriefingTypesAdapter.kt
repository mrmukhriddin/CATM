package ru.metasharks.catm.feature.briefings.ui.main.types

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi

class BriefingTypesAdapter(
    onTypeClick: (BriefingTypeUi) -> Unit
) : ListDelegationAdapter<List<BaseListItem>>() {

    init {
        delegatesManager.addDelegate(BriefingTypeDelegate(onTypeClick))
    }

    fun setTypes(items: List<BaseListItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
