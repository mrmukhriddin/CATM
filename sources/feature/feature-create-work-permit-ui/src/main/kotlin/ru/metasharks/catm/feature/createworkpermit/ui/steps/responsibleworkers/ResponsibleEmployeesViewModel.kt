package ru.metasharks.catm.feature.createworkpermit.ui.steps.responsibleworkers

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.dialog.pick.PickItemViewModel
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.searchmanager.SelectableSearchListManager
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleEmployeesUseCase
import javax.inject.Inject

@HiltViewModel
class ResponsibleEmployeesViewModel @Inject constructor(
    getResponsibleManagersUseCase: GetResponsibleEmployeesUseCase,
    responsibleManagerMapper: ResponsibleEmployeesMapper,
    offlineModeProvider: OfflineModeProvider,
    getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
) : PickItemViewModel<SelectableBaseListItem, PickItemDialog.ItemUi>() {

    override val dataSource = if (offlineModeProvider.isInOnlineMode) {
        ResponsibleEmployeesOnlineDataSource(
            getResponsibleManagersUseCase,
            responsibleManagerMapper
        )
    } else {
        ResponsibleEmployeesOfflineDataSource(
            getWorkPermitCreateOfflineToolsUseCase,
            responsibleManagerMapper
        )
    }

    override val currentItems: MutableLiveData<List<SelectableBaseListItem>> = MutableLiveData()

    override val error: MutableLiveData<Throwable> = MutableLiveData()

    override val callback: SelectableSearchListManager.Callback<SelectableBaseListItem> =
        object : SelectableSearchListManager.Callback<SelectableBaseListItem> {

            override fun onLoading(start: Boolean) = Unit

            override fun onItemSelected(item: SelectableBaseListItem, isSelected: Boolean) {
                _selectedItem.value = item
            }
        }

    override fun search(query: String) {
        searchManager.search(query)
    }

    override fun load(newPage: Boolean) {
        searchManager.load(newPage)
    }
}
