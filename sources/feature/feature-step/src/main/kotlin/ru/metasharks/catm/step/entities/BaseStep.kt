package ru.metasharks.catm.step.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.utils.strings.StringResWrapper

abstract class BaseStep(
    open val number: Int,
    open val description: StringResWrapper,
    var restoreData: RestoreData? = null
) : BaseListItem {

    override val id: String
        get() = number.toString()

    abstract val tag: String
}
