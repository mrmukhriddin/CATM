package ru.metasharks.catm.feature.notifications.entities.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import ru.metasharks.catm.feature.notifications.entities.DataX
import ru.metasharks.catm.feature.notifications.entities.NotificationContent
import ru.metasharks.catm.feature.notifications.entities.NotificationContentTypes

object NotificationContentFromServerSerializer : KSerializer<NotificationContent> {

    private val json = Json

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName = "NotificationContent", kind = PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NotificationContent) {
        encoder.encodeSerializableValue(
            NotificationContent.serializer(),
            value
        )
    }

    override fun deserialize(decoder: Decoder): NotificationContent {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("Expected JsonDecoder for ${decoder::class}")
        val obj = input.decodeJsonElement() as? JsonObject
            ?: throw SerializationException("Expected JsonObject for ${input::class}")

        val type = obj[FIELD_TYPE]?.jsonPrimitive?.content

        val data = json.decodeFromJsonElement<DataX>(obj)

        return when (type) {
            NotificationContentTypes.BRIEFING_INVITE -> {
                NotificationContent.BriefingInvite(
                    message = data.message,
                    notificationId = data.notificationId,
                    createdAt = data.createdAt,
                    briefingId = requireNotNull(data.tags?.briefingId),
                )
            }
            NotificationContentTypes.DOCUMENT_EXPIRED_DIRECTOR -> {
                NotificationContent.DocumentExpiredDirector(
                    message = data.message,
                    notificationId = data.notificationId,
                    createdAt = data.createdAt,
                    userId = requireNotNull(data.tags?.userId)
                )
            }
            NotificationContentTypes.DOCUMENT_EXPIRED_USER -> {
                NotificationContent.DocumentExpiredUser(
                    message = data.message,
                    notificationId = data.notificationId,
                    createdAt = data.createdAt,
                )
            }
            NotificationContentTypes.BRIEFING_WORKER_INVITE -> {
                NotificationContent.BriefingWorkerInvite(
                    message = data.message,
                    notificationId = data.notificationId,
                    createdAt = data.createdAt,
                    userId = requireNotNull(data.tags?.userId),
                    briefingId = requireNotNull(data.tags?.briefingId),
                )
            }
            NotificationContentTypes.WORK_PERMIT_SIGNER_INVITE -> {
                NotificationContent.WorkPermitSignerInvite(
                    message = data.message,
                    notificationId = data.notificationId,
                    createdAt = data.createdAt,
                    workPermitId = requireNotNull(data.tags?.workPermitId)
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown type $type")
            }
        }
    }

    const val FIELD_TYPE = "type"
}
