package ru.metasharks.catm.feature.offline.save.clear

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface ClearAllUseCase {

    operator fun invoke(): Completable
}

internal class ClearAllUseCaseImpl @Inject constructor(
    private val fileManager: FileManager,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val pendingActionsRepository: PendingActionsRepository,
) : ClearAllUseCase {

    override fun invoke(): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapCompletable { user ->
                Single.fromCallable { fileManager.deleteFiles(Paths.getPathForUser(user.id)) }
                    .flatMapCompletable { pendingActionsRepository.clearForCurrentUser() }
            }
//        return Single.fromCallable { fileManager.deleteFiles(Paths.PATH_PROFILE) }
//            .flatMap { Single.fromCallable { fileManager.deleteFiles(Paths.PATH_BRIEFINGS) } }
//            .flatMap { Single.fromCallable { fileManager.deleteFiles(Paths.PATH_BUFFER) } }
//            .flatMap { Single.fromCallable { fileManager.deleteFiles(Paths.PATH_WORK_PERMITS) } }
//            .flatMap { pendingActionsRepository.clear }
//            .ignoreElement()
    }
}
