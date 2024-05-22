package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CreateDailyPermitScreen
import ru.metasharks.catm.core.navigation.screens.DailyPermitsScreen
import ru.metasharks.catm.core.navigation.screens.EndDailyPermitScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionDailyPermitPayload
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitPendingUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.DeleteDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.SignDailyPermitUseCase
import ru.metasharks.catm.utils.requireValue
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class DailyPermitsViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val createDailyPermitScreen: CreateDailyPermitScreen,
    private val endDailyPermitScreen: EndDailyPermitScreen,
    private val signDailyPermitUseCase: SignDailyPermitUseCase,
    private val deleteDailyPermitUseCase: DeleteDailyPermitUseCase,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private var changesCount = -1

    private val _payload = MutableLiveData<Payload>()
    val payload: LiveData<Payload> = _payload

    override fun onWorkPermitSet(workPermit: WorkPermitDetailsUi) {
        val additionalInfo = workPermit.additionalInfo as SignedAdditionalInfo
        changesCount = additionalInfo.dailyPermitsList.size
        if (offlineModeProvider.isInOnlineMode) {
            _payload.value =
                Payload.DailyPermits(additionalInfo.dailyPermitsList, workPermit.isCreator)
        } else {
            pendingActionsRepository.getPayloads(PendingActionPayload::class)
                .map { pendingDailyPermits ->
                    pendingDailyPermits.filterIsInstance<PendingActionPayload.CreateDailyPermit>()
                        .map {
                            DailyPermitPendingUi(permitterSignerName = it.permitterName)
                        } to pendingDailyPermits
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { (pendingDailyPermits, all) ->
                        _payload.value = Payload.DailyPermits(
                            pendingDailyPermits +
                                    additionalInfo.dailyPermitsList.map {
                                        mapper.mapDailyPermitOffline(
                                            it,
                                            all.filterIsInstance<PendingActionDailyPermitPayload>()
                                        )
                                    },
                            workPermit.isCreator
                        )
                    }, {
                        Timber.e(it)
                    }
                )
        }
    }

    fun openCreateDailyPermitScreen(workPermitId: Long) {
        appRouter.navigateToWithResult<Boolean>(createDailyPermitScreen(workPermitId)) { isCreated ->
            if (isCreated) {
                loadByMode(workPermitId, forceRefresh = false)
            }
        }
    }

    private fun loadByMode(workPermitId: Long, forceRefresh: Boolean) {
        if (offlineModeProvider.isInOnlineMode) {
            load(workPermitId, forceRefresh)
        } else {
            loadOffline()
        }
    }

    private fun loadOffline() {
        onWorkPermitSet(workPermit.requireValue)
    }

    override fun exit() {
        appRouter.sendResultBy(DailyPermitsScreen.KEY, changesCount)
        appRouter.exit()
    }

    fun signDailyPermit(item: DailyPermitUi) {
        if (offlineModeProvider.isInOnlineMode) {
            signDailyPermitUseCase(
                workPermitId = workPermitId,
                dailyPermitId = item.dailyPermitId,
                role = item.permitterSigner.role,
                dateEnd = null
            )
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.SignDailyPermit(
                    workPermitId = workPermitId,
                    dailyPermitId = item.dailyPermitId,
                    role = item.permitterSigner.role,
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    loadByMode(workPermitId, forceRefresh = true)
                }, {
                    Timber.e(it)
                }
            )
    }

    fun deleteDailyPermit(item: DailyPermitUi) {
        if (offlineModeProvider.isInOnlineMode) {
            deleteDailyPermitUseCase(workPermitId, item.dailyPermitId)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.DeleteDailyPermit(
                    workPermitId = workPermitId,
                    dailyPermitId = item.dailyPermitId
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    loadByMode(workPermitId, forceRefresh = true)
                }, {
                    Timber.e(it)
                }
            )
    }

    fun openEndDailyPermitScreen(item: DailyPermitUi) {
        appRouter.navigateToWithResult<Boolean>(
            endDailyPermitScreen(
                workPermitId,
                item.dailyPermitId
            )
        ) { updated ->
            if (updated) {
                loadByMode(workPermitId, forceRefresh = false)
            }
        }
    }

    fun init(workPermitId: Long, forceRefresh: Boolean) {
        load(workPermitId, forceRefresh)
    }

    sealed class Payload {

        class DailyPermits(
            val dailyPermitsItem: List<BaseListItem>,
            val isCreator: Boolean
        ) : Payload()
    }
}
