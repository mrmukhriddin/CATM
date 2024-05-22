package ru.metasharks.catm.feature.offline.save.workpermit

import io.reactivex.rxjava3.core.Maybe
import ru.metasharks.catm.feature.offline.db.WorkPermitCreateOfflineToolsDao
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface GetWorkPermitCreateOfflineToolsUseCase {

    operator fun invoke(): Maybe<WorkPermitCreateOfflineTools>
}

internal class GetWorkPermitCreateOfflineToolsUseCaseImpl @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val dao: WorkPermitCreateOfflineToolsDao,
) : GetWorkPermitCreateOfflineToolsUseCase {

    override fun invoke(): Maybe<WorkPermitCreateOfflineTools> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapMaybe { dao.getTools(it.id) }
    }
}
