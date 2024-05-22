package ru.metasharks.catm.feature.briefings.ui.entities.quiz

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class QuestionTrackerItem(
    val index: Int,
    var isCurrent: Boolean?
) : BaseListItem {

    override val id: String
        get() = index.toString()
}
