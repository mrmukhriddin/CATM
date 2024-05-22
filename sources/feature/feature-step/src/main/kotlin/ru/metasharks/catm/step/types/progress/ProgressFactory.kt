package ru.metasharks.catm.step.types.progress

import android.view.ViewGroup
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemProgressBinding
import ru.metasharks.catm.step.StepFactory

class ProgressFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemProgressBinding, Unit>(
    parent,
    R.layout.step_pattern_item_progress,
    { StepPatternItemProgressBinding.bind(it) }
)
