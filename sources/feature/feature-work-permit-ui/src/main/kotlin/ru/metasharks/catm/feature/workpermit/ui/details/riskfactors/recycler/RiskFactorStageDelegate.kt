package ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemRiskFactorStageBinding
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.RiskFactorUi
import ru.metasharks.catm.utils.layoutInflater

class RiskFactorStageDelegate(private val onItemClick: (RiskFactorUi) -> Unit) :
    BaseListItemDelegate<RiskFactorUi, RiskFactorStageDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemRiskFactorStageBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: RiskFactorUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            root.setOnClickListener {
                onItemClick(item)
            }
            if (item.expanded) {
                riskFactorContent.isVisible = true
            } else {
                riskFactorContent.isGone = true
            }
            riskFactorContent.text = item.getFullText()
            riskFactorName.text = item.name
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is RiskFactorUi

    class ViewHolder(val binding: ItemRiskFactorStageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
