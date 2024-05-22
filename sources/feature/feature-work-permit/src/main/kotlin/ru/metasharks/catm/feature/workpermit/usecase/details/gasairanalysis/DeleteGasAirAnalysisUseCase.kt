package ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface DeleteGasAirAnalysisUseCase {

    operator fun invoke(gasAirAnalysisId: Long): Completable
}

internal class DeleteGasAirAnalysisUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : DeleteGasAirAnalysisUseCase {

    override fun invoke(gasAirAnalysisId: Long): Completable {
        return workPermitsApi.deleteGasAirAnalysis(gasAirAnalysisId)
    }
}
