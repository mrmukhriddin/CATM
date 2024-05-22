package ru.metasharks.catm.feature.workpermit.ui.details.riskfactors

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class RiskFactorStageUi(
    val stageId: Int,
    val stageIdName: String,
    val stageName: String,
    val riskFactors: List<RiskFactorUi>
)

data class RiskFactorUi(
    val riskFactorId: Int,
    val name: String,
    val actions: List<ActionUi>,
    var expanded: Boolean = false
) : BaseListItem {

    fun getFullText(): String {
        val sb = StringBuilder()
        actions.forEachIndexed { index, action ->
            if (index != 0) {
                sb.appendLine().appendLine()
            }
            if (action.index != null) {
                sb.append(action.index)
                    .append(". ")
            }
            sb.append(action.text)
            if (action.subActions.isNotEmpty()) {
                sb.appendLine().append(action.subActions.joinToString(("\n")))
            }
        }
        return sb.toString()
    }

    override val id: String
        get() = riskFactorId.toString()
}

data class ActionUi(
    val index: Int?,
    val text: String,
    val subActions: List<String> = emptyList()
)
