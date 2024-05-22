package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.end

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.EndDailyPermitScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.SignDailyPermitUseCase
import javax.inject.Inject

@HiltViewModel
internal class EndDailyPermitViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val signDailyPermitUseCase: SignDailyPermitUseCase,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun endDailyPermit(dailyPermit: DailyPermitUi, dateEnd: String) {
        if (offlineModeProvider.isInOnlineMode) {
            signDailyPermitUseCase(
                workPermitId,
                dailyPermit.dailyPermitId,
                dailyPermit.responsibleSigner.role,
                dateEnd
            )
                .andThen(getWorkPermitUseCase(workPermitId, forceRefresh = true))
                .ignoreElement()
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.EndDailyPermit
                    (
                    dailyPermitId = dailyPermit.dailyPermitId,
                    workPermitId = workPermitId,
                    role = dailyPermit.responsibleSigner.role,
                    dateEnd = dateEnd,
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    appRouter.sendResultBy(EndDailyPermitScreen.KEY, true)
                    appRouter.exit()
                }, {
                    _error.value = it
                }
            )
    }
}
