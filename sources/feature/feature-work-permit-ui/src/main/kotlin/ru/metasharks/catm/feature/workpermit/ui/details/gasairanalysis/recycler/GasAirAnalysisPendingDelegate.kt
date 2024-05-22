package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemGasAirAnalysisBinding
import ru.metasharks.catm.utils.layoutInflater

class GasAirAnalysisPendingDelegate :
    BaseListItemDelegate<GasAirAnalysisPendingUi, GasAirAnalysisPendingDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemGasAirAnalysisBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: GasAirAnalysisPendingUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            val context = root.context
            probeDate.text = context.getString(R.string.waiting_for_connection)
            probeTime.isGone = true
            probePlace.text = getHighlightedString(context, R.string.probe_place, item.probePlace)
            probeComponents.text =
                getHighlightedString(context, R.string.probe_components, item.probeComponents)
            probePermissibleConcentration.text = getHighlightedString(
                context,
                R.string.probe_permissible_concentration,
                item.probePermissibleConcentration
            )
            probeConcentration.text =
                getHighlightedString(context, R.string.probe_concentration, item.probeConcentration)
            probeDevice.text =
                getHighlightedString(context, R.string.probe_device, item.probeDevice)
            probeNext.isGone = true
            probeResponsiblePerson.isGone = true
            probeDelete.isGone = true
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is GasAirAnalysisPendingUi

    class ViewHolder(val binding: ItemGasAirAnalysisBinding) : RecyclerView.ViewHolder(binding.root)
}
