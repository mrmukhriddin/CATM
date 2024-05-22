package ru.metasharks.catm.feature.offline.usecases

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.offline.db.OfflineSaveDao
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface DeleteOfflineUseCase {

    operator fun invoke(): Completable
}

internal class DeleteOfflineUseCaseImpl @Inject constructor(
    private val offlineSaveDao: OfflineSaveDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : DeleteOfflineUseCase {

    override fun invoke(): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapCompletable { offlineSaveDao.deleteForUser(it.id) }
    }
}
