package ru.metasharks.catm.feature.briefings.ui.main.categories

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingCategoryUi

class BriefingCategoriesAdapter(
    onCategoryClick: (BriefingCategoryUi) -> Unit
) : ListDelegationAdapter<List<BaseListItem>>() {

    init {
        delegatesManager.addDelegate(BriefingCategoryDelegate(onCategoryClick))
    }

    fun setCategories(items: List<BaseListItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
