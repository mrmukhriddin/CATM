package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.dialog

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeeMapper
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX

class EmployeeOfflineDataSource(
    private val getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
    private val mapper: EmployeeMapper,
) : SearchListManager.DataSource<EmployeeUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<EmployeeUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase().map {
            mapper.mapEmployeesToUi(it.exceptResponsibleEmployees) to null as String?
        }.toSingle()
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<EmployeeUi>, String?>> {
        return getWorkPermitCreateOfflineToolsUseCase()
            .map { searchAmongList(it.exceptResponsibleEmployees, searchQuery) }
            .map { mapper.mapEmployeesToUi(it) to null as String? }.toSingle()
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
