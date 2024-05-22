package ru.metasharks.catm.feature.notifications.ui.recycler

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.notifications.entities.NotificationContent
import ru.metasharks.catm.feature.notifications.ui.R
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.ButtonActionItemUi
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.ClickPayload
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.InformationItemUi
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject

internal class NotificationRecyclerMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun mapNotificationToUi(content: NotificationContent): BaseListItem {
        val payload = mapPayload(content)
            ?: return InformationItemUi(
                notificationId = content.notificationId,
                message = content.message,
                createdDate = parseISODate(content.createdAt),
            )
        return ButtonActionItemUi(
            notificationId = content.notificationId,
            message = content.message,
            createdDate = parseISODate(content.createdAt),
            payload = payload
        )
    }

    private fun mapPayload(content: NotificationContent): ClickPayload? {
        return when (content) {
            is NotificationContent.WorkPermitSignerInvite -> {
                ClickPayload.WorkPermit(
                    workPermitId = content.workPermitId,
                    buttonText = context.getString(R.string.btn_text_work_permit),
                    chipItem = getChipForWorkPermit(),
                )
            }
            is NotificationContent.BriefingInvite -> {
                ClickPayload.Briefing(
                    briefingId = content.briefingId,
                    buttonText = context.getString(R.string.btn_text_briefing),
                    chipItem = getChipForBriefing(),
                )
            }
            is NotificationContent.DocumentExpiredUser -> {
                ClickPayload.CurrentUser(
                    buttonText = context.getString(R.string.btn_text_current_user),
                    chipItem = getChipForCurrentUser(),
                )
            }
            is NotificationContent.DocumentExpiredDirector -> {
                ClickPayload.User(
                    userId = content.userId,
                    buttonText = context.getString(R.string.btn_text_user),
                    chipItem = getChipForUser(),
                )
            }
            is NotificationContent.BriefingWorkerInvite -> {
                ClickPayload.User(
                    userId = content.userId,
                    buttonText = context.getString(R.string.btn_text_user),
                    chipItem = getChipForUser(),
                )
            }
            else -> null
        }
    }

    private fun getChipForWorkPermit(): ChipItem = getChip(
        text = "Наряд-допуск",
        textColorRes = ru.metasharks.catm.core.ui.R.color.light_gray,
        backgroundRes = android.R.color.transparent,
        strokeColorRes = ru.metasharks.catm.core.ui.R.color.light_gray
    )

    private fun getChipForUser(): ChipItem = getChip(
        text = "Работник",
        textColorRes = ru.metasharks.catm.core.ui.R.color.light_gray,
        backgroundRes = ru.metasharks.catm.core.ui.R.color.light_blue,
    )

    private fun getChipForBriefing(): ChipItem = getChip(
        text = "Инструктаж",
        textColorRes = ru.metasharks.catm.core.ui.R.color.light_gray,
        backgroundRes = ru.metasharks.catm.core.ui.R.color.white
    )

    private fun getChipForCurrentUser(): ChipItem = getChip(
        text = "Документы",
        textColorRes = ru.metasharks.catm.core.ui.R.color.light_gray,
        backgroundRes = ru.metasharks.catm.core.ui.R.color.stroke_gray
    )

    private fun getChip(
        text: String,
        @ColorRes textColorRes: Int,
        @ColorRes backgroundRes: Int,
        @ColorRes strokeColorRes: Int? = null,
    ): ChipItem {
        val textColor = ContextCompat.getColor(context, textColorRes)
        val backgroundColor = ContextCompat.getColor(context, backgroundRes)
        val strokeColor = strokeColorRes?.let { ContextCompat.getColor(context, it) }
        return ChipItem(text, textColor, backgroundColor, strokeColor)
    }

    private fun parseISODate(isoDate: String): String {
        return LocalDateUtils.toString(
            LocalDateUtils.parseISO8601toLocalDateTime(isoDate).toLocalDate()
        )
    }
}
