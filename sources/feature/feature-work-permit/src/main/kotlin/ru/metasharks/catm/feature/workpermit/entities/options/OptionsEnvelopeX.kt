package ru.metasharks.catm.feature.workpermit.entities.options

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OptionsEnvelopeX(

    @SerialName("another_factors")
    val anotherFactors: List<OptionX>,

    @SerialName("dangerous_factors")
    val dangerousFactors: List<OptionX>,

    @SerialName("save_equipment")
    val saveEquipment: List<OptionX>,

    @SerialName("used_equipment")
    val usedEquipment: List<OptionX>,

    @SerialName("work_scheme")
    val workScheme: List<OptionX>
)
