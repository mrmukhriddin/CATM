package ru.metasharks.catm.core.ui.recycler

import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

class BaseUpdateDifferCallback(
    private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    private val actionBeforeNotifying: () -> Unit,
) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        actionBeforeNotifying()
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        actionBeforeNotifying()
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        actionBeforeNotifying()
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        actionBeforeNotifying()
        adapter.notifyItemRangeChanged(position, count, payload)
    }
}
