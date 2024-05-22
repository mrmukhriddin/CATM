package ru.metasharks.catm.feature.offline.pending.usecases

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.offline.db.PendingActionDao
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject
import kotlin.reflect.KClass

fun interface GetPendingActionPayloadsUseCase {

    operator fun invoke(clazz: KClass<out PendingActionPayload>): Single<List<PendingActionPayload>>
}

internal class GetPendingActionsPayloadsUseCaseImpl @Inject constructor(
    private val pendingActionDao: PendingActionDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : GetPendingActionPayloadsUseCase {

    override fun invoke(clazz: KClass<out PendingActionPayload>): Single<List<PendingActionPayload>> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { pendingActionDao.getPendingActionsForUser(it.id) }
            .map { pendingActions ->
                pendingActions.filter { pendingAction ->
                    clazz.isInstance(pendingAction.payload)
                }.map { it.payload }
            }
    }
}
