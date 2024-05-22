package ru.metasharks.catm.feature.offline.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.offline.save.briefings.SaveBriefingsUseCase
import ru.metasharks.catm.feature.offline.save.clear.ClearAllUseCase
import ru.metasharks.catm.feature.offline.save.clear.ClearPendingRequestsUseCase
import ru.metasharks.catm.feature.offline.save.profile.SaveProfileUseCase
import ru.metasharks.catm.feature.offline.save.profile.SaveWorkersUseCase
import ru.metasharks.catm.feature.offline.save.workpermit.SaveWorkPermitsUseCase
import ru.metasharks.catm.feature.offline.usecases.DeleteOfflineUseCase
import ru.metasharks.catm.feature.offline.usecases.GetSavedOfflineUseCase
import ru.metasharks.catm.feature.offline.usecases.SaveOfflineUseCase
import javax.inject.Inject

class SaveRepository @Inject constructor(
    private val getSavedOfflineUseCase: GetSavedOfflineUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val saveWorkPermitsUseCase: SaveWorkPermitsUseCase,
    private val saveWorkersUseCase: SaveWorkersUseCase,
    private val saveBriefingsUseCase: SaveBriefingsUseCase,
    private val saveOfflineUseCase: SaveOfflineUseCase,
    private val deleteOfflineUseCase: DeleteOfflineUseCase,
    private val clearAllUseCase: ClearAllUseCase,
    private val clearPendingRequestsUseCase: ClearPendingRequestsUseCase,
) {

    fun getSaved(): Observable<List<OfflineSave>> {
        return getSavedOfflineUseCase()
    }

    fun getSavedSingle(): Single<List<OfflineSave>> {
        return getSavedOfflineUseCase().first(emptyList())
    }

    fun isTypeSaved(type: SaveType): Single<Boolean> {
        return getSavedSingle().map { offlineSaves ->
            offlineSaves.any { offlineSave ->
                offlineSave.typeCode == type.code
            }
        }
    }

    fun saveProfile(): Completable {
        return saveProfileUseCase(null).toSingle { }
            .flatMapCompletable { saveOfflineUseCase(SaveType.PROFILE) }
    }

    fun saveWorkers(): Completable {
        return saveWorkersUseCase().toSingle { }
            .flatMapCompletable { saveOfflineUseCase(SaveType.WORKERS) }
    }

    fun saveWorkPermits(): Completable {
        return saveWorkPermitsUseCase().toSingle { }
            .flatMapCompletable { saveOfflineUseCase(SaveType.WORK_PERMITS) }
    }

    fun saveBriefings(): Completable {
        return saveBriefingsUseCase().toSingle { }
            .flatMapCompletable { saveOfflineUseCase(SaveType.BRIEFINGS) }
    }

    fun clearAll(): Completable {
        return clearAllUseCase().toSingle {}
            .flatMapCompletable { deleteOfflineUseCase() }
    }

    fun clearForCurrentUser(): Completable {
        return clearPendingRequestsUseCase()
    }
}
