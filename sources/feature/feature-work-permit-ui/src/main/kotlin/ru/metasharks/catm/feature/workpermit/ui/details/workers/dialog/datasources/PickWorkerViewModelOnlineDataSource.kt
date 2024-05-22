package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.datasources

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkerMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase

class PickWorkerViewModelOnlineDataSource(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val mapper: WorkerMapper,
) : SearchListManager.DataSource<WorkerListItemUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<WorkerListItemUi>, String?>> {
        return getEmployeesUseCase.invoke(nextPageUrl, null)
            .map { envelope ->
                envelope.results.map(mapper::mapWorkerToListEntity) to envelope.next
            }
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<WorkerListItemUi>, String?>> {
        return getEmployeesUseCase.invoke(nextPageUrl, searchQuery).map { envelope ->
            envelope.results.map(mapper::mapWorkerToListEntity) to envelope.next
        }
    }
}
