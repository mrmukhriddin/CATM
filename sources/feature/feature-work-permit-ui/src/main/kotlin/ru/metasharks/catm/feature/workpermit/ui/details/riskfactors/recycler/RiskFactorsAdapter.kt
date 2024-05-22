package ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.RiskFactorUi

class RiskFactorsAdapter : PaginationListDelegationAdapter(null) {

    init {
        delegatesManager.addDelegate(RiskFactorStageDelegate(this::onRiskFactorClick))
    }

    private fun onRiskFactorClick(item: RiskFactorUi) {
        val oldList = items ?: return
        val index = oldList.indexOf(item)
        item.expanded = item.expanded.not()
        notifyItemChanged(index)
    }
}
