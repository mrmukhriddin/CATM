package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPermitDetailsX(

    @SerialName("documents")
    val documents: List<WorkPermitDocumentX>,

    @SerialName("managers_signed")
    val managersSigned: Int,

    @SerialName("responsible_manager")
    val responsibleManager: ResponsibleManagerX,

    @SerialName("signers")
    val signers: List<SignerX>,

    @SerialName("work_permit")
    val mainInfo: WorkPermitInnerX,

    @SerialName("workers")
    val workers: List<WorkerX>,

    @SerialName("workers_count")
    val workersCount: Int,

    @SerialName("workers_signed_count")
    val workersSignedCount: Int,

    @SerialName("gas_air_analysis")
    val gasAirAnalysis: List<GasAirAnalysisX>,

    @SerialName("daily_permits")
    val dailyPermits: List<DailyPermitX>,

    @SerialName("extension")
    val extension: ExtensionX?,
) {

    val signersCount: Int
        get() = signers.size
}
