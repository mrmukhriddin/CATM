package ru.metasharks.catm.core.ui.chips

import android.content.res.ColorStateList
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.utils.layoutInflater

object ChipUtils {

    fun setChip(chip: Chip, chipItem: ChipItem) {
        with(chip) {
            text = chipItem.text
            setTextColor(ColorStateList.valueOf(chipItem.textColor))
            chipBackgroundColor = ColorStateList.valueOf(chipItem.backgroundColor)
            chipItem.strokeColor?.let {
                chipStrokeColor = ColorStateList.valueOf(it)
                chipStrokeWidth =
                    context.resources.getDimensionPixelSize(R.dimen.chip_stroke_width).toFloat()
            } ?: run {
                chipStrokeWidth = 0f
            }
        }
    }

    fun addChip(chipGroup: ChipGroup, chipItem: ChipItem) {
        chipGroup.addView(createChip(chipGroup, chipItem))
    }

    fun createChip(parentView: ViewGroup, chipItem: ChipItem): Chip {
        val context = parentView.context
        return (context.layoutInflater
            .inflate(R.layout.item_chip, parentView, false) as Chip)
            .also { setChip(it, chipItem) }
    }
}
