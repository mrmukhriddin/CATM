package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CreateDailyPermitScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit.CreateDailyPermitDataX
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.CreateDailyPermitUseCase
import javax.inject.Inject

@HiltViewModel
internal class CreateDailyPermitViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val createDailyPermitUseCase: CreateDailyPermitUseCase,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
    private val pendingActionsRepository: PendingActionsRepository,
    private val offlineModeProvider: OfflineModeProvider,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun createDailyPermit(data: CreateDailyPermitData) {
        if (offlineModeProvider.isInOnlineMode) {
            createDailyPermitOnline(data)
        } else {
            createDailyPermitOffline(data)
        }
    }

    private fun createDailyPermitOffline(data: CreateDailyPermitData) {
        pendingActionsRepository.savePendingAction(
            PendingActionPayload.CreateDailyPermit(
                createDailyPermitInfo = createWorkPermitData(data),
                workPermitId = data.workPermitId,
                permitterName = data.permitterName,
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    appRouter.sendResultBy(CreateDailyPermitScreen.KEY, true)
                    appRouter.exit()
                }, {
                    _error.value = it
                }
            )
    }

    private fun createDailyPermitOnline(data: CreateDailyPermitData) {
        createDailyPermitUseCase(
            data.workPermitId,
            createWorkPermitData(data)
        ).flatMap {
            getWorkPermitUseCase(data.workPermitId, forceRefresh = true)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    appRouter.sendResultBy(CreateDailyPermitScreen.KEY, true)
                    appRouter.exit()
                }, {
                    _error.value = it
                }
            )
    }

    private fun createWorkPermitData(data: CreateDailyPermitData): CreateDailyPermitDataX {
        return CreateDailyPermitDataX(
            permitterId = data.permitterId,
            date = data.date,
        )
    }
}
