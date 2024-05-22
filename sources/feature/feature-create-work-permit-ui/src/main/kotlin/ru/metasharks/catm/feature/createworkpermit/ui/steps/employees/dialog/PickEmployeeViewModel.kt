package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.searchmanager.SelectableSearchListManager
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeeMapper
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase
import javax.inject.Inject

@HiltViewModel
class PickEmployeeViewModel @Inject constructor(
    getEmployeesUseCase: GetEmployeesUseCase,
    mapper: EmployeeMapper,
    offlineModeProvider: OfflineModeProvider,
    getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
) : ViewModel() {

    private val _selectedItem = MutableLiveData<SelectableBaseListItem>()
    val selectedItem: LiveData<SelectableBaseListItem> = _selectedItem

    private val _adapterItems = MutableLiveData<List<SelectableBaseListItem>>()
    val adapterItems: LiveData<List<SelectableBaseListItem>> = _adapterItems

    private val dataSource = if (offlineModeProvider.isInOnlineMode) {
        EmployeeOnlineDataSource(getEmployeesUseCase, mapper)
    } else {
        EmployeeOfflineDataSource(getWorkPermitCreateOfflineToolsUseCase, mapper)
    }

    private val callback = object : SelectableSearchListManager.Callback<SelectableBaseListItem> {

        override fun onLoading(start: Boolean) = Unit

        override fun onItemSelected(item: SelectableBaseListItem, isSelected: Boolean) {
            _selectedItem.value = item
        }
    }

    private val searchManager: SelectableSearchListManager<SelectableBaseListItem, EmployeeUi> =
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

    fun selectItem(item: EmployeeUi) {
        searchManager.selectItem(item)
    }

    fun setInitialItems(initialPickedEmployees: List<EmployeeUi>) {
        searchManager.setInitialItems(initialPickedEmployees)
    }

    fun getSelectedItems(): List<EmployeeUi> {
        return searchManager.getSelectedItems()
    }
}
