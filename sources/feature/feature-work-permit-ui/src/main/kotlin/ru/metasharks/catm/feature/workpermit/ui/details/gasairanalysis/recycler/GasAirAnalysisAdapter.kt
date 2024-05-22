package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class GasAirAnalysisAdapter(
    onDeleteGasAirAnalysisClick: OnDeleteGasAirAnalysisClick
) : PaginationListDelegationAdapter(null) {

    private var isDeletionAvailable = true

    init {
        delegatesManager.addDelegate(
            GasAirAnalysisDelegate(
                onDeleteGasAirAnalysisClick,
                ::isDeletionAvailable
            )
        )
            .addDelegate(GasAirAnalysisPendingDelegate())
    }

    fun setDeleteAvailable(available: Boolean) {
        isDeletionAvailable = available
    }
}
