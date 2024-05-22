package ru.metasharks.catm.core.ui.recycler

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

fun getItemsWithLoadingState(
    adapterItems: List<BaseListItem>,
    isLoading: Boolean,
    toStart: Boolean = false
): Pair<List<BaseListItem>, Boolean> {
    if (isLoading) {
        val hasLoadingItem = adapterItems.any { it is ProgressItem }
        if (!hasLoadingItem) {
            return if (toStart) {
                listOf(ProgressItem()).plus(adapterItems)
            } else {
                adapterItems.plus(ProgressItem())
            } to true
        }
    } else {
        val indexOfProgressItem = adapterItems.indexOfFirst { it is ProgressItem }
        if (indexOfProgressItem != RecyclerView.NO_POSITION) {
            return adapterItems.toMutableList().apply {
                removeAt(indexOfProgressItem)
            } to true
        }
    }
    return adapterItems to false
}

fun updateIsLoading(
    isLoading: Boolean,
    post: Boolean,
    mutableAdapterItemsLiveData: MutableLiveData<List<BaseListItem>>,
) {
    val currentItems = mutableAdapterItemsLiveData.value ?: emptyList()
    val (newItems, changed) = getItemsWithLoadingState(currentItems, isLoading)

    if (changed) {
        if (post) {
            mutableAdapterItemsLiveData.postValue(newItems)
        } else {
            mutableAdapterItemsLiveData.value = newItems
        }
    }
}
