package ru.metasharks.catm.feature.briefings.ui.main.quiz.recycler

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.databinding.ItemQuestionTrackerBinding
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuestionTrackerItem
import ru.metasharks.catm.utils.layoutInflater

class QuestionTrackerDelegate :
    BaseListItemDelegate<QuestionTrackerItem, QuestionTrackerDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemQuestionTrackerBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: QuestionTrackerItem,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding.root) {
            text = item.index.toString()
            when (item.isCurrent) {
                null -> {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            ru.metasharks.catm.core.ui.R.color.blue_enabled
                        )
                    )
                    background =
                        ContextCompat.getDrawable(context, R.drawable.background_question_tracker)
                    isEnabled = false
                }
                false -> {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            ru.metasharks.catm.core.ui.R.color.blue_enabled
                        )
                    )
                    background =
                        ContextCompat.getDrawable(context, R.drawable.background_question_tracker)
                    isEnabled = true
                }
                true -> {
                    isEnabled = true
                    background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.background_question_tracker_chosen
                        )
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            ru.metasharks.catm.core.ui.R.color.white
                        )
                    )
                }
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is QuestionTrackerItem

    class ViewHolder(val binding: ItemQuestionTrackerBinding) :
        RecyclerView.ViewHolder(binding.root)
}
