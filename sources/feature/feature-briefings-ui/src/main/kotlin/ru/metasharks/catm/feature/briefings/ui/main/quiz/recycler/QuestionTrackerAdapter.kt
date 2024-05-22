package ru.metasharks.catm.feature.briefings.ui.main.quiz.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuestionTrackerItem

class QuestionTrackerAdapter : PaginationListDelegationAdapter(null) {

    fun activate(indexOfQuestion: Int) {
        val oldItems = requireNotNull(items)

        val itemToSet = oldItems[indexOfQuestion] as QuestionTrackerItem
        itemToSet.isCurrent = true
        notifyItemChanged(indexOfQuestion)

        val previousItemIndex = indexOfQuestion - 1
        if (previousItemIndex >= 0) {
            val itemToUnset = oldItems[previousItemIndex] as QuestionTrackerItem
            itemToUnset.isCurrent = false
            notifyItemChanged(previousItemIndex)
        }
    }

    init {
        delegatesManager.addDelegate(QuestionTrackerDelegate())
    }
}
