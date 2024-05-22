package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.dialog

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeeMapper
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase

class EmployeeOnlineDataSource(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val mapper: EmployeeMapper,
) : SearchListManager.DataSource<EmployeeUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<EmployeeUi>, String?>> {
        return getEmployeesUseCase.invoke(nextPageUrl, null)
            .map { envelope ->
                mapper.mapEmployeesToUi(envelope.results) to envelope.next
            }
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<EmployeeUi>, String?>> {
        return getEmployeesUseCase.invoke(nextPageUrl, searchQuery).map { envelope ->
            mapper.mapEmployeesToUi(envelope.results) to envelope.next
        }
    }
}
