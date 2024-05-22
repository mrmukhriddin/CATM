package ru.metasharks.catm.core.ui.recycler.empty.response

import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.DelegateStaticView

object EmptyResponseDelegate : DelegateStaticView<EmptyResponseItem, BaseListItem>(
    EmptyResponseItem::class.java,
    R.layout.item_empty_response
)
