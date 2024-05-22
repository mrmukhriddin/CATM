package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.datasources

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkerMapper

class PickWorkerViewModelOfflineDataSource(
    private val getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
    private val mapper: WorkerMapper,
) : SearchListManager.DataSource<WorkerListItemUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<WorkerListItemUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase()
            .map { tools ->
                tools.exceptResponsibleEmployees.map(mapper::mapWorkerToListEntity) to null as String?
            }.toSingle()
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<WorkerListItemUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase()
            .map { searchAmongList(it.responsibleEmployees, searchQuery) }
            .map { it.map(mapper::mapWorkerToListEntity) to null as String? }
            .toSingle()
    }

    private fun searchAmongList(
        responsibleEmployees: List<WorkPermitUserX>,
        searchQuery: String
    ): List<WorkPermitUserX> {
        return responsibleEmployees.filter {
            it.firstName.contains(searchQuery, ignoreCase = true) || it.lastName.contains(
                searchQuery,
                ignoreCase = true
            )
        }
    }
}
