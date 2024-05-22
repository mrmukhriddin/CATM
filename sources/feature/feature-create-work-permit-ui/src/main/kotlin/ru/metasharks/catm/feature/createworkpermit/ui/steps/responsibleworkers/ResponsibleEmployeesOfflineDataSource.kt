package ru.metasharks.catm.feature.createworkpermit.ui.steps.responsibleworkers

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX

class ResponsibleEmployeesOfflineDataSource(
    private val getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
    private val responsibleManagerMapper: ResponsibleEmployeesMapper,
) : SearchListManager.DataSource<PickItemDialog.ItemUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase().map {
            it.responsibleEmployees.map(responsibleManagerMapper::mapResponsibleManagerToPickItem) to null as String?
        }.toSingle()
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase()
            .map { searchAmongList(it.responsibleEmployees, searchQuery) }
            .map { it.map(responsibleManagerMapper::mapResponsibleManagerToPickItem) to null as String? }
            .toSingle()
    }

    private fun searchAmongList(
        responsibleEmployees: List<WorkPermitUserX>,
        searchQuery: String
    ): List<WorkPermitUserX> {
        return responsibleEmployees.filter {
            it.firstName.contains(searchQuery, ignoreCase = true) || it.lastName.contains(searchQuery, ignoreCase = true)
        }
    }
}
