package ru.metasharks.catm.feature.workpermit.ui.mapper

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.feature.workpermit.entities.riskfactors.ActionX
import ru.metasharks.catm.feature.workpermit.entities.riskfactors.RiskFactorStageX
import ru.metasharks.catm.feature.workpermit.entities.riskfactors.RiskFactorX
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.ActionUi
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.RiskFactorStageUi
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.RiskFactorUi
import javax.inject.Inject

internal class RiskFactorsMapper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun mapStages(stages: List<RiskFactorStageX>): List<RiskFactorStageUi> {
        return stages.map(this::mapStage)
    }

    fun mapStage(stage: RiskFactorStageX): RiskFactorStageUi {
        return RiskFactorStageUi(
            stageId = stage.stageId,
            stageIdName = stage.stageIdName,
            stageName = stage.stageName,
            riskFactors = mapRiskFactors(stage.riskFactors)
        )
    }

    fun mapRiskFactors(riskFactors: List<RiskFactorX>): List<RiskFactorUi> {
        return riskFactors.map(this::mapRiskFactor)
    }

    fun mapRiskFactor(riskFactor: RiskFactorX): RiskFactorUi {
        return RiskFactorUi(
            riskFactorId = riskFactor.id,
            name = riskFactor.riskName,
            actions = mapActions(riskFactor.actions)
        )
    }

    fun mapActions(actions: List<ActionX>): List<ActionUi> {
        return actions.map(this::mapAction)
    }

    fun mapAction(action: ActionX): ActionUi {
        return ActionUi(
            index = action.id,
            text = action.text,
            subActions = action.subactions ?: emptyList()
        )
    }
}
