package ru.metasharks.catm.core.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

typealias IdentityCallback<T> = (T, T) -> Boolean
typealias EqualityCallback<T> = (T, T) -> Boolean
typealias ChangePayloadCallback<T> = (T, T) -> Any?

class RecyclerViewUpdater<T : Any> private constructor(
    private val oldItems: List<T>,
    private val newItems: List<T>,
    private val identityCallback: IdentityCallback<T>,
    private val equalityCallback: EqualityCallback<T>,
    private val changePayloadCallback: ChangePayloadCallback<T>?,
    private val detectMoves: Boolean = true,
) {

    private fun prepareUpdate(): ListUpdateData<T> {
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldItems,
                newItems,
                identityCallback,
                equalityCallback,
                changePayloadCallback
            ),
            detectMoves
        )
        return ListUpdateData(newItems, diffResult)
    }

    fun update(adapter: RecyclerView.Adapter<*>) {
        prepareUpdate().dispatchUpdates(adapter)
    }

    class Builder<T : Any>(
        private val oldItems: List<T>,
        private val newItems: List<T>,
    ) {

        var identityCallback: IdentityCallback<T> = { _, _ -> false }

        var equalityCallback: EqualityCallback<T> = { oldItem, newItem -> oldItem == newItem }

        var changePayloadCallback: ChangePayloadCallback<T>? = null

        var detectMoves: Boolean = true

        fun build(): RecyclerViewUpdater<T> {
            return RecyclerViewUpdater(
                oldItems,
                newItems,
                identityCallback,
                equalityCallback,
                changePayloadCallback,
                detectMoves
            )
        }
    }

    /* interface KeyFunc<T, K> {

        fun getKey(item: T): K
    } */

    private inner class DiffCallback<T>(
        private val oldItems: List<T>,
        private val newItems: List<T>,
        private val identityCallback: IdentityCallback<T>,
        private val equalityCallback: EqualityCallback<T>,
        private val changePayloadCallback: ChangePayloadCallback<T>?,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return identityCallback(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return equalityCallback(oldItem, newItem)
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return changePayloadCallback?.let { callback ->
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                callback(oldItem, newItem)
            }
        }
    }
}
