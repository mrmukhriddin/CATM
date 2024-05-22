package ru.metasharks.catm.core.ui.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ListUpdateData<T : Any>(
    val items: List<T>,
    private val diffResult: DiffUtil.DiffResult? = null,
) {

    @SuppressLint("NotifyDataSetChanged")
    fun dispatchUpdates(adapter: RecyclerView.Adapter<*>) {
        diffResult?.dispatchUpdatesTo(adapter) ?: run {
            adapter.notifyDataSetChanged()
        }
    }
}
