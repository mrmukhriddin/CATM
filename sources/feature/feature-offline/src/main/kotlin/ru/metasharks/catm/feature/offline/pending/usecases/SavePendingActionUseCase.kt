package ru.metasharks.catm.feature.offline.pending.usecases

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.offline.db.PendingActionDao
import ru.metasharks.catm.feature.offline.pending.entities.PendingAction
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface SavePendingActionUseCase {

    operator fun invoke(pendingPayload: PendingActionPayload): Completable
}

internal class SavePendingActionUseCaseImpl @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val pendingActionDao: PendingActionDao,
) : SavePendingActionUseCase {

    override fun invoke(pendingPayload: PendingActionPayload): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .map { PendingAction(userId = it.id, payload = pendingPayload) }
            .flatMapCompletable {
                pendingActionDao.insert(it)
            }
    }
}
