package ru.metasharks.catm.feature.workpermit.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.DailyPermitsScreen
import ru.metasharks.catm.core.navigation.screens.ExtendWorkPermitScreen
import ru.metasharks.catm.core.navigation.screens.GasAirAnalyzesScreen
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.navigation.screens.RiskFactorsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsAdditionalDocumentsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsSignersScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsWorkersScreen
import ru.metasharks.catm.core.network.offline.CurrentUserIdProvider
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.ui.StatusNewMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.StatusSignedMenuEntry
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.DeleteWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.CloseWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.SignExtensionUseCase
import ru.metasharks.catm.feature.workpermit.usecase.sign.CheckIfAllowedSignUseCase
import ru.metasharks.catm.feature.workpermit.usecase.sign.SignWorkPermitUseCase
import ru.metasharks.catm.utils.strings.decodeUtf8
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class WorkPermitDetailsViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
    private val getWorkPermitUseCase: GetWorkPermitUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val mapper: WorkPermitsMapper,
    private val mediaPreviewScreen: MediaPreviewScreen,
    private val workersScreen: WorkPermitDetailsWorkersScreen,
    private val signersScreen: WorkPermitDetailsSignersScreen,
    private val riskFactorsScreen: RiskFactorsScreen,
    private val extendWorkPermitScreen: ExtendWorkPermitScreen,
    private val dailyPermitsScreen: DailyPermitsScreen,
    private val additionalDocumentsScreen: WorkPermitDetailsAdditionalDocumentsScreen,
    private val gasAirAnalysisScreen: GasAirAnalyzesScreen,
    private val deleteWorkPermitUseCase: DeleteWorkPermitUseCase,
    private val signWorkPermitUseCase: SignWorkPermitUseCase,
    private val closeWorkPermitUseCase: CloseWorkPermitUseCase,
    private val signExtensionUseCase: SignExtensionUseCase,
    private val generateWorkPermitPdfUseCase: GenerateWorkPermitPdfUseCase,
    private val checkIfAllowedSignUseCase: CheckIfAllowedSignUseCase,
    private val fileManager: FileManager,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    private var isLoading = false

    private var workPermitId = -1L

    private var _workPermit: WorkPermitDetailsUi? = null
    val workPermit: WorkPermitDetailsUi
        get() = requireNotNull(_workPermit)

    private val _updatePayload = MutableLiveData<Payload>()
    val updatePayload: LiveData<Payload> = _updatePayload

    val availableSignings = mutableSetOf<SignerRole>()

    private val _updatedRole = MutableLiveData<Pair<SignerRole, Boolean>>()
    val updatedRole: LiveData<Pair<SignerRole, Boolean>> = _updatedRole

    fun init(id: Long, refresh: Boolean = false) {
        workPermitId = id
        if (offlineModeProvider.isInOfflineMode) {
            _updatePayload.value = OfflinePayload.OfflineMode
        }
        load(id, refresh)
    }

    private fun load(workPermitId: Long, refresh: Boolean = false) {
        if (offlineModeProvider.isInOnlineMode) {
            loadOnline(workPermitId, refresh)
        } else {
            loadOffline(workPermitId)
        }
    }

    private fun loadOffline(id: Long) {
        Singles.zip(
            getWorkPermitUseCase(id, forceRefresh = offlineModeProvider.isInOnlineMode),
            getCurrentUserUseCase(false)
        )
            .flatMap { (workPermit, user) ->
                Single.just(mapper.mapWorkPermit(workPermit, user))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { workPermitUi ->
                    _workPermit = workPermitUi
                    _updatePayload.value = Payload.LoadedWorkPermit(workPermitUi)
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun loadOnline(id: Long, refresh: Boolean) {
        isLoading = true
        Singles.zip(
            getWorkPermitUseCase(id, forceRefresh = offlineModeProvider.isInOnlineMode),
            getCurrentUserUseCase(false)
        )
            .map { (workPermit, user) ->
                mapper.mapWorkPermit(workPermit, user)
            }
            .doFinally { isLoading = false }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { workPermitUi ->
                    _workPermit = workPermitUi
                    if (refresh) {
                        _updatePayload.value = Payload.StopRefreshIndicator
                    }
                    _updatePayload.value = Payload.LoadedWorkPermit(workPermitUi)
                }, {
                    Timber.e(it)
                }
            )
    }

    fun refresh(workPermitId: Long) {
        if (isLoading) {
            _updatePayload.value = Payload.StopRefreshIndicator
            return
        }
        load(workPermitId, refresh = true)
    }

    fun sendWorkPermitToSigning() {
        if (offlineModeProvider.isInOnlineMode) {
            signWorkPermitUseCase(workPermitId, SignerRole.RESPONSIBLE_MANAGER.code, true)
                .andThen(generateWorkPermitPdfUseCase(workPermitId))
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.SignWorkPermit(workPermitId)
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (offlineModeProvider.isInOnlineMode) {
                        appRouter.sendResultBy(WorkPermitDetailsScreen.KEY, true)
                        loadOnline(workPermitId, refresh = false)
                    } else {
                        _updatePayload.value = OfflinePayload.DeletePending
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    fun signWorkPermit(role: SignerRole) {
        signWorkPermitUseCase(workPermitId, role.code, true)
            .andThen(generateWorkPermitPdfUseCase(workPermitId))
            .andThen(
                Singles.zip(
                    getWorkPermitUseCase(workPermitId, true),
                    getCurrentUserUseCase(false)
                )
            )
            .map { (workPermit, user) ->
                mapper.mapWorkPermit(workPermit, user)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _workPermit = it
                    availableSignings.remove(role)
                    if (it.status == StatusCode.SIGNED) {
                        appRouter.sendResultBy(WorkPermitDetailsScreen.KEY, true)
                        _updatePayload.value = Payload.LoadedWorkPermit(it)
                    } else {
                        _updatePayload.value = Payload.SuccessfulSigning(it, role)
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    fun rejectWorkPermit(role: SignerRole) {
        signWorkPermitUseCase(workPermitId, role.code, false)
            .andThen(generateWorkPermitPdfUseCase(workPermitId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _updatePayload.value = Payload.Reject(workPermit)
                }, {
                    Timber.e(it)
                }
            )
    }

    fun checkEnabledRoles(rolesList: List<SignerRole>) {
        Observable.fromIterable(rolesList)
            .flatMap({ signer ->
                checkIfAllowedSignUseCase(workPermitId, signer.code).toObservable()
            }, { signer, isAllowed ->
                signer to isAllowed
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.second) {
                        availableSignings.add(it.first)
                    }
                    _updatedRole.value = it
                }, {
                    Timber.e(it)
                }
            )
    }

    fun deleteWorkPermit() {
        if (offlineModeProvider.isInOnlineMode) {
            deleteWorkPermitUseCase(workPermitId)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.DeleteWorkPermit(
                    workPermitId = workPermitId
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (offlineModeProvider.isInOnlineMode) {
                        appRouter.sendResultBy(WorkPermitDetailsScreen.KEY, true)
                        appRouter.exit()
                    } else {
                        _updatePayload.value = OfflinePayload.DeletePending
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    fun exit() {
        appRouter.exit()
    }

    fun openPdfFile(fileUrl: String) {
        if (offlineModeProvider.isInOnlineMode) {
            appRouter.navigateTo(mediaPreviewScreen(fileUrl, null))
        } else {
            appRouter.navigateTo(
                mediaPreviewScreen(
                    fileManager.getFileUri(
                        filePath = Paths.getWorkPermitPathForId(
                            userId = currentUserIdProvider(),
                            workPermitId = workPermitId
                        ),
                        fileName = decodeUtf8(getFileNameFromFileUrl(fileUrl))
                    ).toString(),
                    null,
                )
            )
        }
    }

    fun openWorkersScreen() {
        appRouter.navigateToWithResult<Pair<Int, Int>>(workersScreen(workPermitId)) { (count, overall) ->
            _updatePayload.value = Payload.Count(
                menuOptionTag = StatusNewMenuEntry.MENU_OPTION_WORKERS,
                count = count,
                overall = overall,
            )
        }
    }

    fun openSignersScreen() {
        appRouter.navigateTo(signersScreen(workPermitId))
    }

    fun openAdditionalDocumentsScreen() {
        appRouter.navigateToWithResult<Int>(additionalDocumentsScreen(workPermitId)) { updateNumber ->
            _updatePayload.value =
                Payload.Count(
                    menuOptionTag = StatusNewMenuEntry.MENU_OPTION_ADD_DOCUMENTS,
                    count = updateNumber,
                )
        }
    }

    fun openRiskFactorsScreen() {
        appRouter.navigateTo(riskFactorsScreen())
    }

    fun openExtendWorkPermitScreen() {
        appRouter.navigateToWithResult<Boolean>(extendWorkPermitScreen(workPermitId)) {
            if (it) {
                load(workPermitId, refresh = false)
            }
        }
    }

    fun openGasAirAnalysisScreen() {
        appRouter.navigateToWithResult<Int>(gasAirAnalysisScreen(workPermitId)) { updateNumber ->
            _updatePayload.value =
                Payload.Count(
                    menuOptionTag = StatusSignedMenuEntry.MENU_OPTION_GAS_ANALYSIS,
                    count = updateNumber,
                )
        }
    }

    fun openDailyPermitsScreen() {
        appRouter.navigateToWithResult<Int>(dailyPermitsScreen(workPermitId)) { updateNumber ->
            _updatePayload.value =
                Payload.Count(
                    menuOptionTag = StatusSignedMenuEntry.MENU_OPTION_DAILY_PERMITS,
                    count = updateNumber,
                )
        }
    }

    fun closeWorkPermit() {
        if (offlineModeProvider.isInOnlineMode) {
            closeWorkPermitUseCase(workPermitId)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.CloseWorkPermit(
                    workPermitId
                )
            )
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (offlineModeProvider.isInOnlineMode) {
                        appRouter.sendResultBy(WorkPermitDetailsScreen.KEY, true)
                    }
                    load(workPermitId, refresh = true)
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    fun signExtension() {
        if (offlineModeProvider.isInOnlineMode) {
            signExtensionUseCase(workPermitId)
        } else {
            pendingActionsRepository.savePendingAction(
                PendingActionPayload.SignExtensionWorkPermit(workPermitId)
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    load(workPermitId, refresh = false)
                }, {
                    Timber.e(it)
                }
            )
    }

    sealed class Payload {

        object StopRefreshIndicator : Payload()

        class LoadedWorkPermit(val workPermit: WorkPermitDetailsUi) : Payload()

        class Count(val menuOptionTag: String, val count: Int, val overall: Int? = null) : Payload()

        class SuccessfulSigning(val workPermit: WorkPermitDetailsUi, val role: SignerRole) :
            Payload()

        class Reject(val workPermit: WorkPermitDetailsUi) : Payload()
    }

    sealed class OfflinePayload : Payload() {

        object DeletePending : OfflinePayload()

        object OfflineMode : OfflinePayload()
    }
}
