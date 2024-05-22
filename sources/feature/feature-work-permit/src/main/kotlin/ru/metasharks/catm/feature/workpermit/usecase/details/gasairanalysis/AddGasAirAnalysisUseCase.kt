package ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.certain.gasairanalysis.AddGasAirAnalysisEnvelopeX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface AddGasAirAnalysisUseCase {

    operator fun invoke(request: AddGasAirAnalysisEnvelopeX): Single<AddGasAirAnalysisEnvelopeX>
}

internal class AddGasAirAnalysisUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : AddGasAirAnalysisUseCase {

    override fun invoke(request: AddGasAirAnalysisEnvelopeX): Single<AddGasAirAnalysisEnvelopeX> {
        return workPermitsApi.addGasAirAnalysis(request)
    }
}
