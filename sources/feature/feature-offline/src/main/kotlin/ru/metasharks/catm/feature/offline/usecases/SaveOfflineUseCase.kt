package ru.metasharks.catm.feature.offline.usecases

import io.reactivex.rxjava3.core.Completable
import org.joda.time.DateTime
import ru.metasharks.catm.feature.offline.db.OfflineSaveDao
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface SaveOfflineUseCase {

    operator fun invoke(type: SaveType): Completable
}

internal class SaveOfflineUseCaseImpl @Inject constructor(
    private val offlineSaveDao: OfflineSaveDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : SaveOfflineUseCase {

    private fun getOfflineSave(type: SaveType, user: UserDb): OfflineSave {
        val now = DateTime.now().millis
        return OfflineSave(
            typeCode = type.code,
            lastTimeSaved = now,
            userId = user.id
        )
    }

    override fun invoke(type: SaveType): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .map { getOfflineSave(type, it) }
            .flatMapCompletable { offlineSaveDao.save(it) }
    }
}
