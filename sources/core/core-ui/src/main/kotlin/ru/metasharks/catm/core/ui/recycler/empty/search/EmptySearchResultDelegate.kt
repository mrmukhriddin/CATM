package ru.metasharks.catm.core.ui.recycler.empty.search

import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.DelegateStaticView

object EmptySearchResultDelegate : DelegateStaticView<EmptySearchResultItem, BaseListItem>(
    EmptySearchResultItem::class.java,
    R.layout.item_empty_search_result
)
