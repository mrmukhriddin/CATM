package ru.metasharks.catm.core.ui.searchmanager

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.ProgressItem
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultItem
import timber.log.Timber

open class SearchListManager<BT : BaseListItem, T : BT>(
    private val mutableLiveData: MutableLiveData<List<BT>>,
    private val dataSource: DataSource<T>,
    private val callback: Callback,
) {

    protected var isLoading: Boolean = false
        set(value) {
            field = value
            callback.onLoading(field)
        }

    protected var searchQuery: String? = null

    protected val defaultWrapper = ItemsWrapper<T>()
    protected val searchWrapper = ItemsWrapper<T>()

    protected val currentWrapper: ItemsWrapper<T>
        get() {
            return if (isInSearch) {
                searchWrapper
            } else {
                defaultWrapper
            }
        }

    protected val isInSearch: Boolean
        get() = searchQuery != null

    private var excludedItemIds: List<String> = emptyList()

    fun search(searchQuery: String?) {
        if (setSearchQuery(searchQuery).not()) {
            return
        }
        load(nextPage = false, isFromSearch = true)
    }

    fun load(nextPage: Boolean) {
        if (isLoading) {
            return
        }
        if (isInSearch) {
            loadSearch(nextPage)
        } else {
            loadData(nextPage)
        }
    }

    protected fun load(nextPage: Boolean, isFromSearch: Boolean) {
        if (isFromSearch) {
            if (searchQuery == null) {
                returnToDefault()
                return
            }
        }
        load(nextPage)
    }

    protected open fun updateAdapterItems() {
        val finalList = if (isInSearch) {
            currentWrapper.items.ifEmpty { listOf(EmptySearchResultItem()) as List<BT> }
        } else {
            currentWrapper.items
        }
        mutableLiveData.value = finalList
    }

    private fun loadData(nextPage: Boolean) {
        if (nextPage && defaultWrapper.nextPageUrl == null) {
            return
        }
        isLoading = true
        updateIsLoading(isLoading, true, mutableLiveData)
        dataSource.load(defaultWrapper.nextPageUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading = false
                updateIsLoading(isLoading, false, mutableLiveData)
            }
            .subscribe(
                { (newItems, nextPageUrl) ->
                    val itemsToSet = getItemsToSet(newItems)
                    updateItems(defaultWrapper, nextPage, itemsToSet, nextPageUrl)
                    updateAdapterItems()
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun loadSearch(nextPage: Boolean) {
        if (nextPage && searchWrapper.nextPageUrl == null) {
            return
        }
        val currentSearchQuery = searchQuery
        if (currentSearchQuery == null) {
            returnToDefault()
            return
        }
        isLoading = true
        updateIsLoading(isLoading, true, mutableLiveData)
        dataSource.search(currentSearchQuery, searchWrapper.nextPageUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading = false
                updateIsLoading(isLoading, false, mutableLiveData)
            }
            .subscribe(
                { (newItems, nextPageUrl) ->
                    val itemsToSet = getItemsToSet(newItems)
                    updateItems(searchWrapper, nextPage, itemsToSet, nextPageUrl)
                    updateAdapterItems()
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun getItemsToSet(newItems: List<T>): List<T> {
        return if (excludedItemIds.isEmpty()) {
            newItems
        } else {
            newItems.filter { item ->
                excludedItemIds.contains(item.id).not()
            }
        }
    }

    private fun updateIsLoading(
        isLoading: Boolean,
        post: Boolean,
        mutableAdapterItemsLiveData: MutableLiveData<List<BT>>,
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

    private fun getItemsWithLoadingState(
        adapterItems: List<BT>,
        isLoading: Boolean,
        toStart: Boolean = false
    ): Pair<List<BT>, Boolean> {
        if (isLoading) {
            val hasLoadingItem = adapterItems.any { it is ProgressItem }
            if (!hasLoadingItem) {
                return if (toStart) {
                    listOf(ProgressItem()).plus(adapterItems) as List<BT>
                } else {
                    adapterItems.plus(ProgressItem()) as List<BT>
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

    protected open fun returnToDefault() {
        updateAdapterItems()
    }

    /**
     * @return - был ли изменени запрос
     */
    private fun setSearchQuery(searchQuery: String?): Boolean {
        if (searchQuery.isNullOrBlank()) {
            return if (isInSearch) {
                this.searchQuery = null
                true
            } else {
                false
            }
        }
        val newQuery = searchQuery.trim()
        if (this.searchQuery == newQuery) {
            return false
        }
        this.searchQuery = newQuery
        return true
    }

    protected open fun updateItems(
        wrapper: ItemsWrapper<T>,
        nextPage: Boolean,
        newItems: List<T>,
        nextPageUrl: String?
    ) {
        if (nextPage) {
            wrapper.items += newItems
        } else {
            wrapper.items = newItems
        }
        wrapper.nextPageUrl = nextPageUrl
    }

    fun setExcludedItemIds(excludedItemIds: List<String>) {
        this.excludedItemIds = excludedItemIds
    }

    interface Callback {
        fun onLoading(start: Boolean)
    }

    interface DataSource<T : Any> {

        fun load(nextPageUrl: String?): Single<Pair<List<T>, String?>>

        fun search(searchQuery: String, nextPageUrl: String?): Single<Pair<List<T>, String?>>
    }

    class ItemsWrapper<T>(
        var items: List<T> = emptyList(),
        var nextPageUrl: String? = null
    )
}
