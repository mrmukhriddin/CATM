package ru.metasharks.catm.core.ui.recycler

interface BaseListItem {
    val id: String

    fun areSame(newItem: BaseListItem): Boolean {
        return this.javaClass == newItem.javaClass && id == newItem.id
    }
}
