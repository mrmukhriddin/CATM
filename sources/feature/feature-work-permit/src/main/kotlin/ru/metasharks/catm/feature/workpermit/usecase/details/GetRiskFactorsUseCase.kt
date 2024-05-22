package ru.metasharks.catm.feature.workpermit.usecase.details

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.riskfactors.RiskFactorStageX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetRiskFactorsUseCase {

    operator fun invoke(): Single<List<RiskFactorStageX>>
}

internal class GetRiskFactorsUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : GetRiskFactorsUseCase {

    override fun invoke(): Single<List<RiskFactorStageX>> {
        return workPermitsApi.getRiskFactors()
    }
}
