package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CreateGasAirAnalysisScreen
import ru.metasharks.catm.core.navigation.screens.GasAirAnalyzesScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.GasAirAnalysisUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.DeleteGasAirAnalysisUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class GasAirAnalyzesViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val createGasAirAnalysisScreen: CreateGasAirAnalysisScreen,
    private val deleteGasAirAnalysisUseCase: DeleteGasAirAnalysisUseCase,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
    private val gasAirAnalysisMapper: GasAirAnalysisMapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val _adapterItems = MutableLiveData<List<BaseListItem>>()
    val adapterItems: LiveData<List<BaseListItem>> = _adapterItems

    private val _isInOfflineMode = MutableLiveData<Boolean>()
    val isInOfflineMode: LiveData<Boolean> = _isInOfflineMode

    private var changesCount = -1

    fun initGasAirAnalyzes(workPermitId: Long) {
        _isInOfflineMode.value = offlineModeProvider.isInOfflineMode
        load(workPermitId, forceRefresh = false)
    }

    override fun onWorkPermitSet(workPermit: WorkPermitDetailsUi) {
        val additionalInfo = workPermit.additionalInfo as? SignedAdditionalInfo ?: return
        changesCount = additionalInfo.gasAnalysisList.size
        if (offlineModeProvider.isInOnlineMode) {
            _adapterItems.value = additionalInfo.gasAnalysisList
        } else {
            pendingActionsRepository.getPayloads(PendingActionPayload.CreateGasAirAnalysis::class)
                .map { pendingActions ->
                    pendingActions.map { gasAirAnalysisMapper.mapPendingToBaseItemList(it) }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _adapterItems.value = it + additionalInfo.gasAnalysisList
                    }, {
                        Timber.e(it)
                    }
                )
        }
    }

    fun deleteGasAirAnalysis(item: GasAirAnalysisUi) {
        deleteGasAirAnalysisUseCase(item.analysisId)
            .andThen(getWorkPermitUseCase(workPermitId, forceRefresh = true))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    load(workPermitId, forceRefresh = false)
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    fun openCreateGasAirAnalysisScreen(workPermitId: Long) {
        appRouter.navigateToWithResult<Unit>(createGasAirAnalysisScreen(workPermitId)) {
            changesCount++
            load(workPermitId, forceRefresh = false)
        }
    }

    override fun exit() {
        appRouter.sendResultBy(GasAirAnalyzesScreen.KEY, changesCount)
        appRouter.exit()
    }
}
