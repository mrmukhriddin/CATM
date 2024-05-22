package ru.metasharks.catm.feature.offline.ui.sync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.offline.repository.SaveRepository
import ru.metasharks.catm.feature.profile.role.RoleProvider
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val repository: SaveRepository,
    private val offlineModeProvider: OfflineModeProvider,
    private val roleProvider: RoleProvider
) : ViewModel() {

    private val _payload = MutableLiveData<Payload>()
    val payload: LiveData<Payload> = _payload

    fun exit() {
        appRouter.exit()
    }

    fun download(type: SaveType) {
        when (type) {
            SaveType.PROFILE -> repository.saveProfile()
            SaveType.WORKERS -> repository.saveWorkers()
            SaveType.WORK_PERMITS -> repository.saveWorkPermits()
            SaveType.BRIEFINGS -> repository.saveBriefings()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.SavedSuccessful(type)
                }, {
                    onSaveError(it, type)
                }
            )
    }

    private fun getSaved() {
        repository.getSaved()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.OfflineSaves(
                        offlineModeProvider.isInOfflineMode,
                        it
                    )
                }, ::onError
            )
    }

    private fun onError(error: Throwable) {
        _payload.value = Payload.Error(error)
    }

    private fun onSaveError(error: Throwable, type: SaveType) {
        _payload.value = Payload.ErrorOnSave(error, type)
    }

    fun clearPendingRequests() {
        repository.clearForCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.ClearedPendingRequests
                }, ::onError
            )
    }

    fun clearAll() {
        repository.clearAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.ClearedAll
                }, ::onError
            )
    }

    fun init() {
        _payload.value = Payload.Role(roleProvider.currentRole)
        getSaved()
    }

    sealed class Payload {

        class Error(val error: Throwable) : Payload()

        class ErrorOnSave(val error: Throwable, val saveType: SaveType) : Payload()

        class Role(val role: ru.metasharks.catm.feature.profile.role.Role) : Payload()

        class OfflineSaves(
            val isOffline: Boolean,
            val saves: List<OfflineSave>
        ) : Payload()

        class SavedSuccessful(val type: SaveType) : Payload()

        object ClearedAll : Payload()

        object ClearedPendingRequests : Payload()
    }
}
