package ru.metasharks.catm.feature.workpermit.entities.riskfactors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RiskFactorStageX(

    @SerialName("risk_factors")
    val riskFactors: List<RiskFactorX>,

    @SerialName("stage_id")
    val stageId: Int,

    @SerialName("stage_id_name")
    val stageIdName: String,

    @SerialName("stage_name")
    val stageName: String
)
