package ru.metasharks.catm.feature.offline.pending

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.functions.Functions
import ru.metasharks.catm.feature.briefings.usecase.PassQuizUseCase
import ru.metasharks.catm.feature.briefings.usecase.SignBriefingUseCase
import ru.metasharks.catm.feature.offline.db.PendingActionDao
import ru.metasharks.catm.feature.offline.pending.entities.PendingAction
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionWorkPermitPayload
import ru.metasharks.catm.feature.offline.pending.usecases.GetPendingActionPayloadsUseCase
import ru.metasharks.catm.feature.offline.pending.usecases.SavePendingActionUseCase
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.usecase.DeleteWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.CreateWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.CreateDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.DeleteDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.SignDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.AddGasAirAnalysisUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.CloseWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.ExtendWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.SignExtensionUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.SignAllWorkersBriefingUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.UpdateWorkersUseCase
import ru.metasharks.catm.feature.workpermit.usecase.sign.SignWorkPermitUseCase
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.cast

class PendingActionsRepository @Inject constructor(
    private val pendingActionDao: PendingActionDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPendingActionPayloadsUseCase: GetPendingActionPayloadsUseCase,
    private val createWorkPermitUseCase: CreateWorkPermitUseCase,
    private val addGasAirAnalysisUseCase: AddGasAirAnalysisUseCase,
    private val createDailyPermitUseCase: CreateDailyPermitUseCase,
    private val savePendingActionUseCase: SavePendingActionUseCase,
    private val signBriefingUseCase: SignBriefingUseCase,
    private val passQuizUseCase: PassQuizUseCase,
    private val deleteWorkPermitUseCase: DeleteWorkPermitUseCase,
    private val signWorkPermitUseCase: SignWorkPermitUseCase,
    private val generateWorkPermitPdfUseCase: GenerateWorkPermitPdfUseCase,
    private val signDailyPermitUseCase: SignDailyPermitUseCase,
    private val closeWorkPermitUseCase: CloseWorkPermitUseCase,
    private val extendWorkPermitUseCase: ExtendWorkPermitUseCase,
    private val signExtensionUseCase: SignExtensionUseCase,
    private val updateWorkersUseCase: UpdateWorkersUseCase,
    private val signAllWorkersBriefingUseCase: SignAllWorkersBriefingUseCase,
    private val deleteDailyPermitUseCase: DeleteDailyPermitUseCase,
) {

    fun areAnyActionsForWorkPermit(workPermitId: Long): Single<Boolean> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { pendingActionDao.getPendingActionsForUser(it.id) }
            .map { pendingActions ->
                isAnyActionIsForWorkPermitWithId(pendingActions, workPermitId)
            }
    }

    fun isActionForWorkPermit(
        workPermitId: Long,
        actionPayloadClazz: KClass<out Any>
    ): Single<Boolean> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { pendingActionDao.getPendingActionsForUser(it.id) }
            .map { pendingActions ->
                pendingActions.map { it.payload }.filterIsInstance<PendingActionWorkPermitPayload>()
                    .filter { it.workPermitId == workPermitId }
                    .any { actionPayloadClazz.isInstance(it) }
            }
    }

    private fun isAnyActionIsForWorkPermitWithId(
        actions: List<PendingAction>,
        workPermitId: Long
    ): Boolean {
        return actions.map { it.payload }.filterIsInstance<PendingActionWorkPermitPayload>()
            .any { it.workPermitId == workPermitId }
    }

    fun savePendingAction(pendingActionPayload: PendingActionPayload): Completable {
        return savePendingActionUseCase(pendingActionPayload)
    }

    fun <T : PendingActionPayload> getPayloads(clazz: KClass<T>): Single<List<T>> {
        return getPendingActionPayloadsUseCase(clazz).map { list ->
            list.map { clazz.cast(it) }
        }
    }

    fun areAnyPending(): Single<Boolean> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { pendingActionDao.havePending(it.id) }
    }

    fun sendAll(): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { pendingActionDao.getPendingActionsForUser(it.id) }
            .flattenAsObservable(Functions.identity())
            .flatMapCompletable { action ->
                sendPendingAction(action).toSingle { }
                    .flatMapCompletable {
                        pendingActionDao.delete(action)
                    }
            }
    }

    @Suppress("LongMethod")
    private fun sendPendingAction(action: PendingAction): Completable {
        val completableFromPending: Completable = when (val payload = action.payload) {
            is PendingActionPayload.CreateWorkPermit -> {
                createWorkPermitUseCase(payload.createWorkPermitInfo).ignoreElement()
            }
            is PendingActionPayload.CreateGasAirAnalysis -> {
                addGasAirAnalysisUseCase(payload.createGasAirAnalysisInfo).ignoreElement()
            }
            is PendingActionPayload.SignBriefing -> {
                signBriefingUseCase(payload.briefingId)
            }
            is PendingActionPayload.PassQuiz -> {
                passQuizUseCase(payload.passQuizRequest).ignoreElement()
            }
            is PendingActionPayload.DeleteWorkPermit -> {
                deleteWorkPermitUseCase(payload.workPermitId)
            }
            is PendingActionPayload.SignWorkPermit -> {
                signWorkPermitUseCase(
                    payload.workPermitId,
                    SignerRole.RESPONSIBLE_MANAGER.code,
                    true
                )
                    .andThen(generateWorkPermitPdfUseCase(payload.workPermitId))
            }
            is PendingActionPayload.CreateDailyPermit -> {
                createDailyPermitUseCase(
                    workPermitId = payload.workPermitId,
                    request = payload.createDailyPermitInfo,
                ).ignoreElement()
            }
            is PendingActionPayload.SignDailyPermit -> {
                signDailyPermitUseCase(
                    workPermitId = payload.workPermitId,
                    dailyPermitId = payload.dailyPermitId,
                    role = payload.role,
                    dateEnd = null,
                )
            }
            is PendingActionPayload.EndDailyPermit -> {
                signDailyPermitUseCase(
                    workPermitId = payload.workPermitId,
                    dailyPermitId = payload.dailyPermitId,
                    role = payload.role,
                    dateEnd = payload.dateEnd,
                )
            }
            is PendingActionPayload.CloseWorkPermit -> {
                closeWorkPermitUseCase(workPermitId = payload.workPermitId)
            }
            is PendingActionPayload.ExtendWorkPermit -> {
                extendWorkPermitUseCase(
                    workPermitId = payload.workPermitId,
                    data = payload.data,
                )
            }
            is PendingActionPayload.SignExtensionWorkPermit -> {
                signExtensionUseCase(workPermitId = payload.workPermitId)
            }
            is PendingActionPayload.AddNewWorkers -> {
                updateWorkersUseCase(workPermitId = payload.workPermitId, request = payload.request)
            }
            is PendingActionPayload.SignNewWorkers -> {
                signAllWorkersBriefingUseCase(briefingId = payload.briefingId)
            }
            is PendingActionPayload.DeleteDailyPermit -> {
                deleteDailyPermitUseCase(
                    workPermitId = payload.workPermitId,
                    dailyPermitId = payload.dailyPermitId
                )
            }
        }
        return completableFromPending.onErrorComplete()
    }

    fun clearForCurrentUser(): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapCompletable { pendingActionDao.deleteForUser(it.id) }
    }
}
