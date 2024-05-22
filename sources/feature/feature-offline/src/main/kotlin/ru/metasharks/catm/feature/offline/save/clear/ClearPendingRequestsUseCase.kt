package ru.metasharks.catm.feature.offline.save.clear

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import javax.inject.Inject

fun interface ClearPendingRequestsUseCase {

    operator fun invoke(): Completable
}

internal class ClearPendingRequestsUseCaseImpl @Inject constructor(
    private val pendingActionsRepository: PendingActionsRepository,
) : ClearPendingRequestsUseCase {

    override fun invoke(): Completable {
        return pendingActionsRepository.clearForCurrentUser()
    }
}
