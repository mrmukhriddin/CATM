package ru.metasharks.catm.feature.profile.ui.workers.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.feature.profile.ui.databinding.ItemWorkerBinding

class DelegateWorker(
    private val onWorkerClick: (workerUI: WorkerUI) -> Unit
) : BaseListItemDelegate<WorkerUI, DelegateWorker.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(item: WorkerUI, holder: ViewHolder, payloads: List<Any?>) {
        holder.bind(item)
        holder.binding.root.setOnClickListener { onWorkerClick(item) }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkerUI

    class ViewHolder(val binding: ItemWorkerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(workerUI: WorkerUI) {
            with(binding) {
                profileCoreInfoContainer.profileName.text =
                    getFullNameWithLineBreak(workerUI.firstName, workerUI.lastName)
                profileCoreInfoContainer.profilePosition.text = workerUI.position.orEmpty()
                profileCoreInfoContainer.profileImageContainer.setData(
                    getFullName(workerUI.firstName, workerUI.lastName),
                    workerUI.avatar
                ).load()
            }
        }
    }
}
