package ru.metasharks.catm.feature.workpermit.ui.details.extend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.ExtendWorkPermitScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.certain.extend.ExtendWorkPermitDataX
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.ExtendWorkPermitUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ExtendWorkPermitViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val extendWorkPermitUseCaseUseCase: ExtendWorkPermitUseCase,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun extend(workPermitId: Long, data: ExtendWorkPermitData) {
        val request = ExtendWorkPermitDataX(
            permitIssuerId = data.permitIssuerId,
            dateEnd = data.date,
        )
        if (offlineModeProvider.isInOnlineMode) {
            extendWorkPermitUseCaseUseCase(workPermitId, request)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.ExtendWorkPermit(workPermitId, request)
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.d("EXTENDED")
                    appRouter.sendResultBy(ExtendWorkPermitScreen.KEY, true)
                    appRouter.exit()
                }, {
                    _error.value = it
                }
            )
    }
}
