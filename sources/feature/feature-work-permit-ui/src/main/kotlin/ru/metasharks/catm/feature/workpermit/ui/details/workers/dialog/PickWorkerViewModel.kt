package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.searchmanager.SelectableSearchListManager
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.datasources.PickWorkerViewModelOfflineDataSource
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.datasources.PickWorkerViewModelOnlineDataSource
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkerMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase
import javax.inject.Inject

@HiltViewModel
class PickWorkerViewModel @Inject constructor(
    getEmployeesUseCase: GetEmployeesUseCase,
    getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
    mapper: WorkerMapper,
    private val offlineModeProvider: OfflineModeProvider,
) : ViewModel() {

    private val _selectedItem = MutableLiveData<SelectableBaseListItem>()
    val selectedItem: LiveData<SelectableBaseListItem> = _selectedItem

    private val _adapterItems = MutableLiveData<List<SelectableBaseListItem>>()
    val adapterItems: LiveData<List<SelectableBaseListItem>> = _adapterItems

    private val dataSource = if (offlineModeProvider.isInOnlineMode) {
        PickWorkerViewModelOnlineDataSource(getEmployeesUseCase, mapper)
    } else {
        PickWorkerViewModelOfflineDataSource(getWorkPermitCreateOfflineToolsUseCase, mapper)
    }

    private val callback = object : SelectableSearchListManager.Callback<SelectableBaseListItem> {

        override fun onLoading(start: Boolean) = Unit

        override fun onItemSelected(item: SelectableBaseListItem, isSelected: Boolean) {
            _selectedItem.value = item
        }
    }

    private val searchManager: SelectableSearchListManager<SelectableBaseListItem, WorkerListItemUi> =
        SelectableSearchListManager(
            _adapterItems,
            dataSource,
            callback
        )

    fun load(nextPage: Boolean) {
        searchManager.load(nextPage)
    }

    fun search(searchQuery: String) {
        searchManager.search(searchQuery)
    }

    fun selectItem(item: WorkerListItemUi) {
        searchManager.selectItem(item)
    }

    fun setInitialItems(initialPickedEmployees: List<WorkerListItemUi>) {
        searchManager.setInitialItems(initialPickedEmployees)
    }

    fun getSelectedItems(): List<WorkerListItemUi> {
        return searchManager.getSelectedItems()
    }

    fun setExcludedItems(excludedWorkerIds: List<String>) {
        searchManager.setExcludedItemIds(excludedWorkerIds)
    }
}
