package ru.metasharks.catm.feature.briefings.ui.entities.quiz

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class QuestionUi(
    val questionId: Long,
    val questionText: String,
    val options: List<OptionUi>
) : BaseListItem {

    override val id: String
        get() = questionId.toString()
}

data class OptionUi(
    val optionId: Long,
    val text: String,
) : BaseListItem {

    override val id: String
        get() = optionId.toString()
}
