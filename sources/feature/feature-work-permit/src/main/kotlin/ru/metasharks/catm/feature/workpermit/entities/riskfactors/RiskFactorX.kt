package ru.metasharks.catm.feature.workpermit.entities.riskfactors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RiskFactorX(

    @SerialName("actions")
    val actions: List<ActionX>,

    @SerialName("id")
    val id: Int,

    @SerialName("risk_name")
    val riskName: String
)
