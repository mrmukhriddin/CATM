package ru.metasharks.catm.feature.workpermit.ui.mapper

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.empty.response.EmptyResponseItem
import ru.metasharks.catm.core.ui.utils.getShortFormOfFullName
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionDailyPermitPayload
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitX
import ru.metasharks.catm.feature.workpermit.entities.certain.DailyPermitX
import ru.metasharks.catm.feature.workpermit.entities.certain.DocumentX
import ru.metasharks.catm.feature.workpermit.entities.certain.ExtensionX
import ru.metasharks.catm.feature.workpermit.entities.certain.GasAirAnalysisX
import ru.metasharks.catm.feature.workpermit.entities.certain.SignerX
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDocumentX
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkerX
import ru.metasharks.catm.feature.workpermit.entities.certain.upload.UploadAdditionalDocumentResponseX
import ru.metasharks.catm.feature.workpermit.ui.entities.AdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi
import ru.metasharks.catm.feature.workpermit.ui.entities.SignerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.entities.UserListItemUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitInnerUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkerUi
import ru.metasharks.catm.feature.workpermit.ui.entities.archived.ArchivedAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.pending.PendingAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.ExtensionUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.GasAirAnalysisUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.list.WorkPermitPendingUi
import ru.metasharks.catm.feature.workpermit.ui.list.WorkPermitUi
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject
import kotlin.reflect.KClass

internal class WorkPermitsMapper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workerMapper: WorkerMapper,
    private val pendingActionsRepository: PendingActionsRepository,
    private val offlineModeProvider: OfflineModeProvider,
) {

    fun mapWorkPermits(workPermits: List<WorkPermitX>): List<BaseListItem> {
        return if (workPermits.isEmpty()) {
            listOf(EmptyResponseItem())
        } else {
            workPermits.map(this::mapWorkPermit)
        }
    }

    fun mapWorkPermitPending(pending: PendingActionPayload.CreateWorkPermit): WorkPermitPendingUi {
        val info = pending.createWorkPermitInfo
        return WorkPermitPendingUi(
            address = info.place,
            workersCount = info.workersIds.size
        )
    }

    fun mapWorkPermit(workPermitX: WorkPermitX): WorkPermitUi {
        return WorkPermitUi(
            permitId = workPermitX.id,
            createdDate = LocalDateUtils.parseToLocalDate(workPermitX.created),
            // it doesn't come from back so we don't show it
            extendDate = null,
            chips = getChips(workPermitX),
            workTypeChip = getWorkTypeChip(workPermitX),
            address = workPermitX.place,
            workersCount = workPermitX.workersCount,
            statusCode = mapStatus(workPermitX.status),
        )
    }

    fun mapWorkPermit(workPermitX: WorkPermitDetailsX, user: UserDb): WorkPermitDetailsUi {
        val status = mapStatus(workPermitX.mainInfo.status)
        return WorkPermitDetailsUi(
            mainInfo = WorkPermitInnerUi(
                permitId = workPermitX.mainInfo.id,
                createdDate = LocalDateUtils.parseToLocalDate(workPermitX.mainInfo.created),
                startDate = LocalDateUtils.parseToLocalDate(workPermitX.mainInfo.startDate),
                startTime = LocalDateUtils.parseToLocalTime(workPermitX.mainInfo.startTime),
                endDate = LocalDateUtils.parseToLocalDate(workPermitX.mainInfo.endDate),
                endTime = LocalDateUtils.parseToLocalTime(workPermitX.mainInfo.endTime),
                chips = getChips(workPermitX.mainInfo.status, workPermitX.mainInfo.statusName),
                workTypeChip = getWorkTypeChip(workPermitX),
                address = workPermitX.mainInfo.place,
                workersCount = workPermitX.workersCount,
                briefingId = workPermitX.mainInfo.briefingId,
            ),
            document = mapDocument(workPermitX.mainInfo.document),
            status = status,
            workersCount = workPermitX.workersCount,
            workersSignedCount = workPermitX.workersSignedCount,
            signersCount = workPermitX.signersCount,
            signersSignedCount = workPermitX.managersSigned,
            additionalDocumentsCount = workPermitX.documents.size,
            workers = mapWorkers(workPermitX.workers),
            signers = mapSigners(workPermitX.signers),
            additionalDocuments = mapDocuments(workPermitX.documents),
            isCreator = isUserCreator(workPermitX, user),
            isPermitter = isRole(workPermitX, user, SignerRole.PERMITTER.code),
            additionalInfo = getAdditionalInfo(workPermitX, user, status),
            offlinePendingActionsAreOut = arePendingActionsAreOut(workPermitX.mainInfo.id)
        )
    }

    private fun arePendingActionsAreOut(id: Long): Boolean {
        return pendingActionsRepository.areAnyActionsForWorkPermit(id).blockingGet()
    }

    @Suppress("LongMethod")
    private fun getAdditionalInfo(
        workPermit: WorkPermitDetailsX,
        user: UserDb,
        status: StatusCode
    ): AdditionalInfo? {
        return when (status) {
            StatusCode.PENDING -> {
                val ifAnyRejected = workPermit.signers.any { it.signed == false }
                PendingAdditionalInfo(
                    rolesList = SignerRole.values().filter { isRole(workPermit, user, it.code) }
                        .map {
                            val signer =
                                workPermit.findRole(it.code) ?: throw IllegalArgumentException()
                            it to signer.signed
                        },
                    anyRejected = ifAnyRejected
                )
            }
            StatusCode.SIGNED -> {
                val isCreator = isUserCreator(workPermit, user)
                SignedAdditionalInfo(
                    gasAnalysisList = workPermit.gasAirAnalysis.map {
                        mapGasAirAnalysis(
                            it,
                            isCreator = isCreator
                        )
                    },
                    dailyPermitsList = workPermit.dailyPermits.map {
                        mapDailyPermit(
                            it, user,
                            isCreator = isCreator
                        )
                    },
                    extension = mapExtension(workPermit.extension, user),
                    offlinePendingClose = isActionPending(
                        workPermit.mainInfo.id,
                        PendingActionPayload.CloseWorkPermit::class,
                    ),
                    offlinePendingExtend = isActionPending(
                        workPermit.mainInfo.id,
                        PendingActionPayload.ExtendWorkPermit::class,
                    ),
                    offlinePendingSignExtension = isActionPending(
                        workPermit.mainInfo.id,
                        PendingActionPayload.SignExtensionWorkPermit::class,
                    ),
                    offlinePendingAddWorkersAction = isActionPending(
                        workPermit.mainInfo.id,
                        PendingActionPayload.AddNewWorkers::class
                    ),
                    offlinePendingSignWorkersAction = isActionPending(
                        workPermit.mainInfo.id,
                        PendingActionPayload.SignNewWorkers::class
                    ),
                )
            }
            StatusCode.ARCHIVED -> {
                val isCreator = isUserCreator(workPermit, user)
                ArchivedAdditionalInfo(
                    gasAnalysisList = workPermit.gasAirAnalysis.map {
                        mapGasAirAnalysis(
                            it,
                            isCreator = isCreator
                        )
                    },
                    dailyPermitsList = workPermit.dailyPermits.map {
                        mapDailyPermit(
                            it, user,
                            isCreator = isCreator
                        )
                    },
                )
            }
            else -> null
        }
    }

    private fun isActionPending(workPermitId: Long, clazz: KClass<out Any>): Boolean {
        return pendingActionsRepository.isActionForWorkPermit(
            workPermitId, clazz
        ).blockingGet()
    }

    private fun mapDailyPermit(
        item: DailyPermitX,
        user: UserDb,
        isCreator: Boolean
    ): DailyPermitUi {
        val dateStartSplit = item.dateStart.split(' ')
        val dateEndSplit = item.dateEnd?.split(' ')
        val signer = mapSigner(item.signers.first { it.role == SignerRole.PERMITTER.code })
        return DailyPermitUi(
            dailyPermitId = item.id,
            dateStart = dateStartSplit[0],
            timeStart = dateStartSplit[1],
            dateEnd = dateEndSplit?.get(0),
            timeEnd = dateEndSplit?.get(1),
            responsibleSigner = mapSigner(item.signers.first { it.role == SignerRole.RESPONSIBLE_MANAGER.code }),
            permitterSigner = signer,
            isCreator = isCreator,
            isSigner = signer.user.userId == user.id,
            isOffline = offlineModeProvider.isInOfflineMode,
        )
    }

    fun mapDailyPermitOffline(
        item: DailyPermitUi,
        pendingActions: List<PendingActionDailyPermitPayload>
    ): DailyPermitUi {
        item.pendingActionSent = pendingActions.any { it.dailyPermitId == item.dailyPermitId }
        return item
    }

    private fun mapExtension(extension: ExtensionX?, user: UserDb): ExtensionUi? {
        if (extension == null) {
            return null
        }
        val signerIssuer = extension.signers.find { it.role == SignerRole.PERMIT_ISSUER.code }
        check(signerIssuer != null) {
            "It should not be null"
        }
        val isAwaiting = signerIssuer.signed == null
        val isSigner = signerIssuer.user.id == user.id
        return ExtensionUi(
            id = extension.id,
            dateEnd = extension.dateEnd,
            isAwaiting = isAwaiting,
            isSigner = isSigner
        )
    }

    private fun mapGasAirAnalysis(item: GasAirAnalysisX, isCreator: Boolean): GasAirAnalysisUi {
        return GasAirAnalysisUi(
            analysisId = item.id,
            isCreator = isCreator,
            date = LocalDateUtils.parseToLocalDate(item.date),
            probeTime = LocalDateUtils.parseToLocalTime(item.time),
            probeNext = LocalDateUtils.parseToLocalDate(item.dateNext),
            probeComponents = item.components,
            probeConcentration = item.result,
            probeDeviceModel = item.deviceModel,
            probeDeviceNumber = item.deviceNumber,
            probePermissibleConcentration = item.concentration,
            probePlace = item.place,
            probeResponsiblePerson = getShortFormOfFullName(
                item.user.lastName,
                item.user.firstName,
                item.user.middleName
            )
        )
    }

    private fun WorkPermitDetailsX.findRole(role: String): SignerX? {
        return signers.find { it.role == role }
    }

    private fun isRole(workPermit: WorkPermitDetailsX, user: UserDb, role: String): Boolean {
        val signer = workPermit.findRole(role) ?: return false
        return signer.user.id == user.id
    }

    fun mapDocuments(documents: List<WorkPermitDocumentX>): List<DocumentUi> {
        return documents.map(this::mapDocument)
    }

    fun mapWorkers(workers: List<WorkerX>): List<WorkerUi> {
        return workers.map(workerMapper::mapWorker)
    }

    fun mapSigners(signers: List<SignerX>): List<SignerListItemUi> {
        return signers.map(this::mapSigner)
    }

    fun mapSigner(signer: SignerX): SignerListItemUi {
        return SignerListItemUi(
            user = UserListItemUi(
                userId = signer.user.id,
                name = signer.user.firstName,
                middleName = signer.user.middleName,
                surname = signer.user.lastName,
                avatar = signer.user.avatar,
                isReady = signer.signed,
                position = signer.user.position
            ),
            role = signer.role,
            roleName = signer.roleLabel,
            signed = signer.signed
        )
    }

    fun mapStatus(status: String): StatusCode {
        return StatusCode.values().first { it.code == status }
    }

    fun mapDocument(document: DocumentX?): DocumentUi? {
        return document?.let {
            return DocumentUi(
                fileId = it.id,
                fileName = it.title,
                fileSizeBytes = it.fileSize,
                fileUrl = it.fileUrl
            )
        }
    }

    fun mapDocument(document: WorkPermitDocumentX): DocumentUi {
        return DocumentUi(
            fileId = document.id,
            fileName = document.fileName,
            fileSizeBytes = document.fileSize,
            fileUrl = document.fileUrl,
        )
    }

    fun mapDocument(documentResponse: UploadAdditionalDocumentResponseX): DocumentUi {
        return DocumentUi(
            fileId = documentResponse.id,
            fileUrl = documentResponse.fileUrl,
            fileName = documentResponse.fileName,
            fileSizeBytes = documentResponse.fileSize,
        )
    }

    private fun getWorkTypeChip(workPermit: WorkPermitX): ChipItem {
        return getWorkTypeChip(workPermit.workType.title)
    }

    private fun getWorkTypeChip(workPermit: WorkPermitDetailsX): ChipItem {
        return getWorkTypeChip(workPermit.mainInfo.workType.title)
    }

    private fun getChips(workPermit: WorkPermitX): List<ChipItem> {
        return getChips(workPermit.status, workPermit.statusName)
    }

    private fun isUserCreator(workPermitX: WorkPermitDetailsX, user: UserDb): Boolean {
        return user.id == workPermitX.responsibleManager.id
    }

    private fun getWorkTypeChip(workType: String): ChipItem {
        val chipColor =
            ContextCompat.getColor(context, ru.metasharks.catm.core.ui.R.color.light_blue)
        val textColor =
            ContextCompat.getColor(context, ru.metasharks.catm.core.ui.R.color.dark_gray)
        return ChipItem(
            text = workType,
            backgroundColor = chipColor,
            textColor = textColor,
            strokeColor = null,
        )
    }

    private fun getChips(status: String, statusName: String): List<ChipItem> {
        val chipColor = getColorForStatus(status)
        val textColor = getTextColorForStatus(status)
        val strokeColor = getStrokeColorForStatus(status)
        val statusChip = ChipItem(
            text = statusName,
            backgroundColor = chipColor,
            textColor = textColor,
            strokeColor = strokeColor,
        )
        return listOf(statusChip)
    }

    @ColorInt
    private fun getColorForStatus(statusCode: String): Int {
        return when (statusCode) {
            StatusCode.NEW.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.bright_gray
            )
            StatusCode.PENDING.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.yellow
            )
            StatusCode.SIGNED.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.light_green
            )
            StatusCode.ARCHIVED.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.background_gray
            )
            else -> ContextCompat.getColor(context, ru.metasharks.catm.core.ui.R.color.cyan)
        }
    }

    @ColorInt
    private fun getTextColorForStatus(statusCode: String): Int {
        return when (statusCode) {
            StatusCode.NEW.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.dark_gray
            )
            StatusCode.PENDING.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.dark_gray
            )
            StatusCode.SIGNED.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.white
            )
            StatusCode.ARCHIVED.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.light_gray
            )
            else -> ContextCompat.getColor(context, ru.metasharks.catm.core.ui.R.color.white)
        }
    }

    @ColorInt
    private fun getStrokeColorForStatus(statusCode: String): Int? {
        return when (statusCode) {
            StatusCode.ARCHIVED.code -> ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.light_gray
            )
            else -> null
        }
    }
}
