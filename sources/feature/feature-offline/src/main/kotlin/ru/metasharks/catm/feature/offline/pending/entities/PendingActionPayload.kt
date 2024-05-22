package ru.metasharks.catm.feature.offline.pending.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.briefings.entities.quiz.request.PassQuizRequestX
import ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit.CreateDailyPermitDataX
import ru.metasharks.catm.feature.workpermit.entities.certain.extend.ExtendWorkPermitDataX
import ru.metasharks.catm.feature.workpermit.entities.certain.gasairanalysis.AddGasAirAnalysisEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.certain.workers.UpdateWorkersRequestX
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitInfo

@Serializable
sealed class PendingActionPayload {

    @Serializable
    @SerialName(PendingActionTypes.CREATE_WORK_PERMIT)
    class CreateWorkPermit(
        val createWorkPermitInfo: CreateWorkPermitInfo,
    ) : PendingActionPayload()

    @Serializable
    @SerialName(PendingActionTypes.CREATE_GAS_AIR_ANALYSIS)
    class CreateGasAirAnalysis(
        val createGasAirAnalysisInfo: AddGasAirAnalysisEnvelopeX,
    ) : PendingActionPayload()

    @Serializable
    @SerialName(PendingActionTypes.CREATE_DAILY_PERMIT)
    class CreateDailyPermit(
        val workPermitId: Long,
        val createDailyPermitInfo: CreateDailyPermitDataX,
        val permitterName: String,
    ) : PendingActionPayload()

    @Serializable
    @SerialName(PendingActionTypes.DELETE_DAILY_PERMIT)
    class DeleteDailyPermit(
        override val dailyPermitId: Long,
        val workPermitId: Long,
    ) : PendingActionPayload(), PendingActionDailyPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.SIGN_DAILY_PERMIT)
    class SignDailyPermit(
        override val dailyPermitId: Long,
        val workPermitId: Long,
        val role: String,
    ) : PendingActionPayload(), PendingActionDailyPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.END_DAILY_PERMIT)
    class EndDailyPermit(
        override val dailyPermitId: Long,
        val workPermitId: Long,
        val role: String,
        val dateEnd: String,
    ) : PendingActionPayload(), PendingActionDailyPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.SIGN_BRIEFING)
    class SignBriefing(
        val briefingId: Int,
    ) : PendingActionPayload()

    @Serializable
    @SerialName(PendingActionTypes.PASS_QUIZ)
    class PassQuiz(
        val passQuizRequest: PassQuizRequestX,
    ) : PendingActionPayload()

    @Serializable
    @SerialName(PendingActionTypes.DELETE_WORK_PERMIT)
    class DeleteWorkPermit(
        override val workPermitId: Long,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.SIGN_WORK_PERMIT)
    class SignWorkPermit(
        override val workPermitId: Long,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.CLOSE_WORK_PERMIT)
    class CloseWorkPermit(
        override val workPermitId: Long
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.EXTEND_WORK_PERMIT)
    class ExtendWorkPermit(
        override val workPermitId: Long,
        val data: ExtendWorkPermitDataX,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.SIGN_EXTENSION_WORK_PERMIT)
    class SignExtensionWorkPermit(
        override val workPermitId: Long,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    @Serializable
    @SerialName(PendingActionTypes.ADD_NEW_WORKERS)
    class AddNewWorkers(
        override val workPermitId: Long,
        val request: UpdateWorkersRequestX,
        val workers: List<Worker>,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload {

        @Serializable
        class Worker(
            val userId: Long,
            val name: String,
            val surname: String,
            val isReady: Boolean,
            val avatar: String?,
            val position: String?,
        )
    }

    @Serializable
    @SerialName(PendingActionTypes.SIGN_NEW_WORKERS)
    class SignNewWorkers(
        override val workPermitId: Long,
        val briefingId: Long,
    ) : PendingActionPayload(), PendingActionWorkPermitPayload

    object PendingActionTypes {

        const val CREATE_WORK_PERMIT = "create_work_permit"
        const val CREATE_GAS_AIR_ANALYSIS = "create_gas_air_analysis"
        const val CREATE_DAILY_PERMIT = "create_daily_permit"
        const val DELETE_DAILY_PERMIT = "delete_daily_permit"
        const val SIGN_DAILY_PERMIT = "sign_daily_permit"
        const val END_DAILY_PERMIT = "end_daily_permit"
        const val SIGN_BRIEFING = "sign_briefing"
        const val PASS_QUIZ = "pass_quiz"
        const val DELETE_WORK_PERMIT = "delete_work_permit"
        const val SIGN_WORK_PERMIT = "sign_work_permit"
        const val CLOSE_WORK_PERMIT = "close_work_permit"
        const val EXTEND_WORK_PERMIT = "extend_work_permit"
        const val SIGN_EXTENSION_WORK_PERMIT = "sign_extension_work_permit"
        const val ADD_NEW_WORKERS = "add_new_workers"
        const val SIGN_NEW_WORKERS = "sign_new_workers"
    }
}
