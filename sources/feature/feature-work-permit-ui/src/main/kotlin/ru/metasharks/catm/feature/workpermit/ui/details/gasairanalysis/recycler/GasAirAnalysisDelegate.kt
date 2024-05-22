package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemGasAirAnalysisBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.GasAirAnalysisUi
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.layoutInflater

typealias OnDeleteGasAirAnalysisClick = (GasAirAnalysisUi) -> Unit

class GasAirAnalysisDelegate(
    private val onDeletionClick: OnDeleteGasAirAnalysisClick,
    private val isDeletionAvailable: () -> Boolean,
) :
    BaseListItemDelegate<GasAirAnalysisUi, GasAirAnalysisDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemGasAirAnalysisBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: GasAirAnalysisUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            val context = root.context
            probeDate.text = LocalDateUtils.toString(item.date)
            probeTime.text = getHighlightedString(
                context,
                R.string.probe_time,
                LocalDateUtils.toString(item.probeTime)
            )
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
            probeNext.text = getHighlightedString(
                context,
                R.string.probe_next,
                LocalDateUtils.toString(item.probeNext)
            )
            probeResponsiblePerson.text = getHighlightedString(
                context,
                R.string.probe_responsible_person,
                item.probeResponsiblePerson
            )
            with(probeDelete) {
                if (item.isCreator) {
                    isVisible = true
                    setOnClickListener {
                        onDeletionClick(item)
                    }
                } else {
                    setOnClickListener(null)
                    isGone = true
                }
            }
            probeDelete.isEnabled = isDeletionAvailable()
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is GasAirAnalysisUi

    class ViewHolder(val binding: ItemGasAirAnalysisBinding) : RecyclerView.ViewHolder(binding.root)
}
