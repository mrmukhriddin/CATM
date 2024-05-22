package ru.metasharks.catm.feature.workpermit.ui.details.dialogs

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.feature.workpermit.ui.mapper.ResponsibleEmployeesMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleEmployeesUseCase

class ResponsibleEmployeesOnlineDataSource(
    private val getResponsibleManagersUseCase: GetResponsibleEmployeesUseCase,
    private val responsibleManagerMapper: ResponsibleEmployeesMapper,
) : SearchListManager.DataSource<PickItemDialog.ItemUi> {

    override fun load(nextPageUrl: String?): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
        return getResponsibleManagersUseCase(nextPageUrl, null).map {
            it.results.map(responsibleManagerMapper::mapResponsibleManagerToPickItem) to it.next
        }
    }

    override fun search(
        searchQuery: String,
        nextPageUrl: String?,
    ): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
        return getResponsibleManagersUseCase(nextPageUrl, searchQuery).map {
            it.results.map(responsibleManagerMapper::mapResponsibleManagerToPickItem) to it.next
        }
    }
}
