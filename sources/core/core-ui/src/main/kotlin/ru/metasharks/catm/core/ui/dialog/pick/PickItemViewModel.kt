package ru.metasharks.catm.core.ui.dialog.pick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.core.ui.searchmanager.SelectableSearchListManager

abstract class PickItemViewModel<BT : SelectableBaseListItem, T : BT> : ViewModel() {

    abstract val currentItems: MutableLiveData<List<BT>>
    abstract val error: MutableLiveData<Throwable>

    abstract val dataSource: SearchListManager.DataSource<T>

    abstract val callback: SelectableSearchListManager.Callback<BT>

    var initialItem: T? = null

    @Suppress("VariableNaming")
    protected val _selectedItem = MutableLiveData<BT>()
    val selectedItem: LiveData<BT> = _selectedItem

    protected val searchManager by lazy {
        SelectableSearchListManager(
            mutableLiveData = currentItems,
            dataSource = dataSource,
            callback = callback,
            mode = SelectableSearchListManager.ONLY_ONE
        )
    }

    val pickedItem: T?
        get() = searchManager.getSelectedItems().firstOrNull()

    abstract fun search(query: String)

    abstract fun load(newPage: Boolean)

    open fun load(newPage: Boolean, initialItem: T?) {
        initialItem?.let {
            this.initialItem = it
            searchManager.setInitialItems(listOf(it))
        }
        load(newPage)
    }

    fun select(item: T) {
        searchManager.selectItem(item)
    }
}
