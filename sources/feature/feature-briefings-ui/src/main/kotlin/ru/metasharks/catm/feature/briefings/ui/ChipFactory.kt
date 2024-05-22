package ru.metasharks.catm.feature.briefings.ui

import android.content.Context
import androidx.core.content.ContextCompat
import ru.metasharks.catm.core.ui.chips.ChipItem

object ChipFactory {

    fun createChip(context: Context, type: Type): ChipItem {
        return when (type) {
            Type.WAITING -> createWaitingChip(context)
            Type.PASSED -> createPassedChip(context)
        }
    }

    private fun createWaitingChip(context: Context): ChipItem {
        return ChipItem(
            text = "Ожидает",
            textColor = ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.black
            ),
            backgroundColor = ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.yellow
            )
        )
    }

    private fun createPassedChip(context: Context): ChipItem {
        return ChipItem(
            text = "Пройдено",
            textColor = ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.white
            ),
            backgroundColor = ContextCompat.getColor(
                context,
                ru.metasharks.catm.core.ui.R.color.light_green
            )
        )
    }

    enum class Type {
        WAITING, PASSED
    }
}
