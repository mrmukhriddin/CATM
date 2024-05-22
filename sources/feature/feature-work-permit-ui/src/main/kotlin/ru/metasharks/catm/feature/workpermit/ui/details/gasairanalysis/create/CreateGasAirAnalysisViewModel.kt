package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CreateGasAirAnalysisScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.certain.gasairanalysis.AddGasAirAnalysisEnvelopeX
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.AddGasAirAnalysisUseCase
import javax.inject.Inject

@HiltViewModel
internal class CreateGasAirAnalysisViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val addGasAirAnalysisUseCase: AddGasAirAnalysisUseCase,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
    private val pendingActionsRepository: PendingActionsRepository,
    private val offlineModeProvider: OfflineModeProvider,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun getAddGasAirAnalysisRequest(data: CreateGasAirAnalysisData): Single<AddGasAirAnalysisEnvelopeX> {
        return getCurrentUserUseCase(false)
            .map {
                AddGasAirAnalysisEnvelopeX(
                    workPermitId = data.workPermitId,
                    userId = it.id,
                    date = data.date,
                    place = data.place,
                    result = data.result,
                    components = data.components,
                    concentration = data.concentration,
                    dateNext = data.dateNext,
                    deviceModel = data.deviceModel,
                    deviceNumber = data.deviceNumber,
                )
            }
    }

    private fun processOfflineCreation(data: AddGasAirAnalysisEnvelopeX): Completable {
        return pendingActionsRepository.savePendingAction(
            PendingActionPayload.CreateGasAirAnalysis(data)
        )
    }

    private fun processOnlineCreation(data: AddGasAirAnalysisEnvelopeX): Completable {
        return addGasAirAnalysisUseCase(data)
            .flatMapCompletable {
                getWorkPermitUseCase(
                    data.workPermitId,
                    forceRefresh = true
                ).ignoreElement()
            }
    }

    fun addAirAnalysis(data: CreateGasAirAnalysisData) {
        getAddGasAirAnalysisRequest(data)
            .flatMapCompletable {
                if (offlineModeProvider.isInOnlineMode) {
                    processOnlineCreation(it)
                } else {
                    processOfflineCreation(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    appRouter.sendResultBy(CreateGasAirAnalysisScreen.KEY, Unit)
                    appRouter.exit()
                }, { error ->
                    _error.value = error
                }
            )
    }
}
