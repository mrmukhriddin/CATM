package ru.metasharks.catm.feature.workpermit.ui.details.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsWorkersScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.PickWorkerDialog
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.ButtonStateMapper
import ru.metasharks.catm.feature.workpermit.ui.mapper.OfflineWorkersMapper
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.ui.mapper.request.UpdateWorkersMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.SignAllWorkersBriefingUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.UpdateWorkersUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class WorkersViewModel @Inject constructor(
    override val appRouter: ApplicationRouter,
    private val updateWorkersMapper: UpdateWorkersMapper,
    private val offlineWorkersMapper: OfflineWorkersMapper,
    private val buttonStateMapper: ButtonStateMapper,
    private val updateWorkersUseCase: UpdateWorkersUseCase,
    private val signAllWorkersBriefingUseCase: SignAllWorkersBriefingUseCase,
    private val generateWorkPermitPdfUseCase: GenerateWorkPermitPdfUseCase,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    var newItems: List<WorkerListItemUi> = emptyList()
    var defaultButtonState: ButtonState? = null

    val originalToReplacementUserId: MutableMap<Long, Long> = mutableMapOf()

    var items: List<BaseListItem> = emptyList()

    private val _workerUserIdToRefresh = MutableLiveData<Long>()
    val workerUserIdToRefresh: LiveData<Long> = _workerUserIdToRefresh

    private val _buttonState = MutableLiveData<ButtonState>(ButtonState.Hidden)
    val buttonState: LiveData<ButtonState> = _buttonState

    private val _adapterItems = MutableLiveData<List<BaseListItem>>()
    val adapterItems: LiveData<List<BaseListItem>> = _adapterItems

    override fun onWorkPermitSet(workPermit: WorkPermitDetailsUi) {
        if (workPermit.status == StatusCode.SIGNED) {
            newItems = emptyList()
            defaultButtonState = buttonStateMapper.getButtonState(workPermit, offlineModeProvider.isInOfflineMode)
            refreshNewItemsState()
        }
    }

    private fun refreshNewItemsState() {
        if (newItems.isEmpty()) {
            _buttonState.value = defaultButtonState
        } else {
            _buttonState.value = ButtonState.NewWorkersAdded
        }
        items = workPermit.value?.workers.orEmpty() + newItems
        if (offlineModeProvider.isInOnlineMode) {
            _adapterItems.value = items
        } else {
            setOfflineWorkers()
        }
    }

    fun addNewWorkers() {
        if (offlineModeProvider.isInOnlineMode) {
            addNewWorkersOnline()
        } else {
            addNewWorkersOffline()
        }
    }

    private fun addNewWorkersOnline() {
        val request = updateWorkersMapper.mapToRequest(newItems)
        updateWorkersUseCase(workPermitId, request)
            .andThen(generateWorkPermitPdfUseCase(workPermitId))
            .andThen(getWorkPermitUseCase(workPermitId, forceRefresh = true))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    sendResult(it)
                    load(workPermitId, forceRefresh = false)
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun addNewWorkersOffline() {
        val request = updateWorkersMapper.mapToRequest(newItems)
        pendingActionsRepository.savePendingAction(
            PendingActionPayload.AddNewWorkers(
                workPermitId = workPermitId,
                request = request,
                workers = newItems.map(offlineWorkersMapper::mapFromUiToOffline),
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    load(workPermitId, forceRefresh = false)
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun setOfflineWorkers() {
        pendingActionsRepository.getPayloads(PendingActionPayload.AddNewWorkers::class)
            .map { newWorkers ->
                newWorkers.fold(emptyList<BaseListItem>()) { a, b ->
                    a + b.workers.map(offlineWorkersMapper::mapFromOfflineToPendingUi)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _adapterItems.value = it + items
                }, {
                    Timber.e(it)
                }
            )
    }

    fun signNewWorkers(briefingId: Long) {
        if (offlineModeProvider.isInOnlineMode) {
            signAllWorkersBriefingUseCase(briefingId)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.SignNewWorkers(
                    workPermitId,
                    briefingId
                )
            )
        }
            .andThen(generateWorkPermitPdfUseCase(workPermitId))
            .andThen(getWorkPermitUseCase(workPermitId, forceRefresh = true))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (offlineModeProvider.isInOnlineMode) {
                        sendResult(it)
                    }
                    load(workPermitId, forceRefresh = false)
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun sendResult(workPermitDetailsX: WorkPermitDetailsX) {
        appRouter.sendResultBy(
            WorkPermitDetailsWorkersScreen.KEY,
            workPermitDetailsX.workersSignedCount to workPermitDetailsX.workersCount
        )
    }

    fun selectItem(item: WorkerListItemUi, selected: Boolean) {
        newItems = if (selected) {
            newItems.plus(item)
        } else {
            val userId = originalToReplacementUserId[item.userId]
            userId?.let { refreshReplacingWorker(item.userId, it, add = false) }
            newItems.filter { it.id != item.id }
        }
        refreshNewItemsState()
    }

    fun replace(
        item: WorkerListItemUi,
        replacingWorkerInfo: PickWorkerDialog.ChangePayload.ReplacingWorkerInfo
    ) {
        newItems =
            if (item.replacingWorkerId == null) { // we changing just now added worker
                newItems.filter { it.id != replacingWorkerInfo.replacingWorkerUserId.toString() }
                    .plus(item)
            } else { // we changing worker that was added to work permit
                if (originalToReplacementUserId.containsKey(item.replacingWorkerId)) {
                    // we change worker that is not in work permit
                    newItems.plus(item)
                } else {
                    refreshReplacingWorker(
                        item.userId,
                        replacingWorkerInfo.replacingWorkerUserId,
                        add = true
                    )
                    newItems.filter { it.id != replacingWorkerInfo.replacingWorkerUserId.toString() }
                        .plus(item)
                }
            }
        refreshNewItemsState()
    }

    private fun refreshReplacingWorker(
        originalUserId: Long,
        replacingWorkerUserId: Long,
        add: Boolean
    ) {
        val userToReplace: Long
        if (add) {
            originalToReplacementUserId[originalUserId] = replacingWorkerUserId
            userToReplace = replacingWorkerUserId
        } else {
            userToReplace =
                originalToReplacementUserId.remove(originalUserId) ?: replacingWorkerUserId
        }
        _workerUserIdToRefresh.value = userToReplace
    }

    sealed class ButtonState {

        object NewWorkersAdded : ButtonState()

        class SignNewWorkers(
            val isAvailable: Boolean,
            val briefingId: Long,
        ) : ButtonState()

        object Hidden : ButtonState()

        class PendingAction(
            val isPendingSign: Boolean = false,
            val isPendingAdd: Boolean = false,
        ) : ButtonState()
    }
}
