package ru.metasharks.catm.feature.offline.usecases

import io.reactivex.rxjava3.core.Observable
import ru.metasharks.catm.feature.offline.db.OfflineSaveDao
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface GetSavedOfflineUseCase {

    operator fun invoke(): Observable<List<OfflineSave>>
}

internal class GetSavedOfflineUseCaseImpl @Inject constructor(
    private val offlineSaveDao: OfflineSaveDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : GetSavedOfflineUseCase {

    override fun invoke(): Observable<List<OfflineSave>> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapObservable { offlineSaveDao.offlineSaves(it.id) }
    }
}
