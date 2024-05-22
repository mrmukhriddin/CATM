package ru.metasharks.catm.feature.notifications.ui.recycler.entities

import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem

internal data class ButtonActionItemUi(
    val notificationId: Long,
    val message: String,
    val createdDate: String,
    val payload: ClickPayload,
) : BaseListItem {

    override val id: String
        get() = notificationId.toString()
}

internal sealed class ClickPayload {

    abstract val chipItem: ChipItem

    abstract val buttonText: String

    class WorkPermit(
        val workPermitId: Long,
        override val buttonText: String,
        override val chipItem: ChipItem
    ) : ClickPayload()

    class Briefing(
        val briefingId: Long,
        override val buttonText: String,
        override val chipItem: ChipItem
    ) : ClickPayload()

    class User(
        val userId: Long,
        override val buttonText: String,
        override val chipItem: ChipItem
    ) : ClickPayload()

    class CurrentUser(
        override val buttonText: String,
        override val chipItem: ChipItem
    ) : ClickPayload()
}
