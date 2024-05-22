package ru.metasharks.catm.feature.profile.entities.license

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.joda.time.LocalDateTime
import ru.metasharks.catm.feature.profile.serializers.LocalDateTimeSerializer

@Serializable
internal class LicenseDetailsX(

    @SerialName("date_expire")
    @Serializable(LocalDateTimeSerializer::class)
    val expireDate: LocalDateTime,
)
