package ru.metasharks.catm.feature.profile.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joda.time.LocalDateTime
import ru.metasharks.catm.utils.date.LocalDateUtils

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(serialName = "date_expire", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val value = decoder.decodeString()
        return LocalDateUtils.parseISO8601toLocalDateTime(value)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(LocalDateUtils.toISO8601String(value))
    }
}
