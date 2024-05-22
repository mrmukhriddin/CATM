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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.metasharks.catm.feature.notifications.entities.BaseNotificationEnvelopeX
import ru.metasharks.catm.feature.notifications.entities.NotificationTypes

object BaseNotificationEnvelopeSerializer : KSerializer<BaseNotificationEnvelopeX> {

    private val json = Json

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName = "NotificationContent", kind = PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BaseNotificationEnvelopeX) {
        encoder.encodeSerializableValue(
            BaseNotificationEnvelopeX.serializer(),
            value
        )
    }

    override fun deserialize(decoder: Decoder): BaseNotificationEnvelopeX {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("Expected JsonDecoder for ${decoder::class}")
        val obj = input.decodeJsonElement() as? JsonObject
            ?: throw SerializationException("Expected JsonObject for ${input::class}")

        val type = obj[FIELD_TYPE]?.jsonPrimitive?.content

        return when (type) {
            NotificationTypes.NOTIFICATION -> {
                BaseNotificationEnvelopeX.Notification(
                    content = json.decodeFromJsonElement(
                        NotificationContentFromServerSerializer,
                        requireNotNull(obj[FIELD_DATA]?.jsonObject)
                    )
                )
            }
            NotificationTypes.UNREAD_NOTIFICATIONS -> {
                BaseNotificationEnvelopeX.UnreadNotifications(
                    notifications = obj[FIELD_DATA]?.jsonArray?.map {
                        json.decodeFromJsonElement(NotificationContentFromServerSerializer, it)
                    }.orEmpty()
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown type $type")
            }
        }
    }

    const val FIELD_TYPE = "type"
    const val FIELD_DATA = "data"
}
