package ru.metasharks.catm.core.ui.searchmanager

import androidx.lifecycle.MutableLiveData
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem

class SelectableSearchListManager<BT : SelectableBaseListItem, T : BT>(
    mutableLiveData: MutableLiveData<List<BT>>,
    dataSource: DataSource<T>,
    private val callback: Callback<BT>,
    private val mode: Int = MANY
) : SearchListManager<BT, T>(
    mutableLiveData, dataSource, callback
) {

    interface Callback<T : SelectableBaseListItem> : SearchListManager.Callback {

        fun onItemSelected(item: T, isSelected: Boolean)
    }

    private val selectedItems: MutableList<T> = mutableListOf()

    fun selectItem(item: T) {
        val selectedItemIndex = selectedItems.indexOfFirst { it.id == item.id }
        when (mode) {
            ONLY_ONE -> {
                if (selectedItemIndex == -1) { // item is not selected case
                    if (selectedItems.isNotEmpty()) { // another item is already selected
                        val deselectedItem = selectedItems.removeFirst()
                        deselectedItem.isSelected = false
                        callback.onItemSelected(deselectedItem, deselectedItem.isSelected)
                    }
                    selectedItems.add(item)
                    item.isSelected = true
                } else { // item is selected case
                    selectedItems.removeAt(selectedItemIndex)
                    item.isSelected = false
                }
            }
            MANY -> {
                // item found case
                if (selectedItemIndex != -1) {
                    selectedItems.removeAt(selectedItemIndex)
                    item.isSelected = false
                } else {
                    selectedItems.add(item)
                    item.isSelected = true
                }
            }
            CERTAIN_AMOUNT -> TODO("IMPLEMENT THIS TYPE OF SELECT")
        }
        callback.onItemSelected(item, item.isSelected)
    }

    fun getSelectedItems(): List<T> {
        return selectedItems
    }

    override fun returnToDefault() {
        checkSelectedItems(currentWrapper.items)
        super.returnToDefault()
    }

    private fun checkSelectedItems(items: List<T>) {
        items.map { item ->
            item.isSelected = selectedItems.any { it.id == item.id }
        }
    }

    override fun updateItems(
        wrapper: ItemsWrapper<T>,
        nextPage: Boolean,
        newItems: List<T>,
        nextPageUrl: String?
    ) {
        checkSelectedItems(newItems)
        super.updateItems(wrapper, nextPage, newItems, nextPageUrl)
    }

    fun setInitialItems(initialSelectedItems: List<T>) {
        selectedItems.addAll(initialSelectedItems)
    }

    companion object {

        const val ONLY_ONE = 1
        const val MANY = 2
        const val CERTAIN_AMOUNT = 3
    }
}
