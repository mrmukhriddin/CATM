package ru.metasharks.catm.feature.profile.ui.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.ProfileScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultItem
import ru.metasharks.catm.core.ui.recycler.getItemsWithLoadingState
import ru.metasharks.catm.feature.profile.ui.entities.mapper.Mapper
import ru.metasharks.catm.feature.profile.ui.workers.recycler.WorkerUI
import ru.metasharks.catm.feature.profile.usecase.GetWorkersOfflineUseCase
import ru.metasharks.catm.feature.profile.usecase.GetWorkersUseCase
import ru.metasharks.catm.feature.profile.usecase.SearchWorkersUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class WorkerViewModel @Inject constructor(
    private val getWorkersUseCase: GetWorkersUseCase,
    private val getWorkersOfflineUseCase: GetWorkersOfflineUseCase,
    private val searchWorkersUseCase: SearchWorkersUseCase,
    private val appRouter: ApplicationRouter,
    private val mapper: Mapper,
    private val profileScreen: ProfileScreen,
    private val offlineModeProvider: OfflineModeProvider,
) : ViewModel() {

    private var nextUrl: String? = null
    private var searchNextUrl: String? = null

    private var searchQuery: String? = null
        set(value) {
            field = value
            _inSearch.value = field != null
        }

    private val _isInOfflineLiveData = MutableLiveData<Boolean>()
    val isInOfflineLiveData: LiveData<Boolean> = _isInOfflineLiveData

    private val isInOffline: Boolean
        get() = isInOfflineLiveData.value ?: false

    private val hasSearchQuery: Boolean
        get() = searchQuery != null

    private val _adapterItems = MutableLiveData<List<BaseListItem>>()
    val adapterItems: LiveData<List<BaseListItem>> = _adapterItems

    private val _adapterSearchItems = MutableLiveData<List<BaseListItem>>()
    val adapterSearchItems: LiveData<List<BaseListItem>> = _adapterSearchItems

    private val _inSearch = MutableLiveData<Boolean>()
    val inSearch: LiveData<Boolean> = _inSearch

    private val _loadingSearch = MutableLiveData<Boolean>()
    val loadingSearch: LiveData<Boolean> = _loadingSearch

    private var isLoading: Boolean = false

    fun loadWorkers(loadNewPage: Boolean) {
        if (loadNewPage && nextUrl == null) {
            return
        }
        val currentItems = _adapterItems.value ?: emptyList()
        updateIsLoading(
            isLoading = true,
            post = true,
            mutableAdapterItemsLiveData = _adapterItems,
        )
        if (loadNewPage) {
            getWorkersUseCase(nextUrl)
        } else {
            getWorkersUseCase(null)
        }
            .map { envelope ->
                envelope.workers.map(mapper::mapWorker) to envelope.next
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading = false
            }
            .subscribe(
                { (newWorkers, nextPageUrl) ->
                    this.nextUrl = nextPageUrl
                    if (loadNewPage) { // pagination: adding new workers to the end of the list
                        _adapterItems.value = currentItems + newWorkers
                    } else { // initial load
                        _adapterItems.value = newWorkers
                    }
                }, {
                    Timber.e(it)
                    updateIsLoading(
                        isLoading = false,
                        post = false,
                        mutableAdapterItemsLiveData = _adapterItems,
                    )
                }
            )
    }

    private fun searchWorkers(query: String?, loadNewPage: Boolean) {
        if (query == null) {
            _adapterSearchItems.postValue(emptyList())
            return
        }
        if (loadNewPage && searchNextUrl == null) {
            return
        }
        val currentItems = _adapterSearchItems.value ?: emptyList()
        updateIsLoading(
            isLoading = true,
            post = true,
            mutableAdapterItemsLiveData = _adapterSearchItems,
            search = true,
        )
        searchWorkersUseCase(query, searchNextUrl)
            .map { envelope ->
                envelope.workers.map(mapper::mapWorker) to envelope.next
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading = false
            }
            .subscribe(
                { (newWorkers, nextPageUrl) ->
                    this.searchNextUrl = nextPageUrl
                    // pagination: adding new workers to the end of the list
                    if (loadNewPage) {
                        _adapterSearchItems.value = currentItems + newWorkers
                    } else {
                        if (newWorkers.isEmpty()) {
                            _adapterSearchItems.value = listOf(EmptySearchResultItem())
                        } else {
                            _adapterSearchItems.value = newWorkers
                        }
                    }
                    _loadingSearch.postValue(false)
                }, {
                    Timber.e(it)
                    updateIsLoading(
                        isLoading = false,
                        post = false,
                        mutableAdapterItemsLiveData = _adapterSearchItems,
                        search = true,
                    )
                }
            )
    }

    private fun updateIsLoading(
        isLoading: Boolean,
        post: Boolean,
        mutableAdapterItemsLiveData: MutableLiveData<List<BaseListItem>>,
        search: Boolean = false,
    ) {
        this.isLoading = isLoading
        val currentItems = mutableAdapterItemsLiveData.value ?: emptyList()
        val (newItems, changed) = getItemsWithLoadingState(currentItems, isLoading)
        if (search) {
            _loadingSearch.postValue(isLoading)
            return
        }

        if (changed) {
            if (post) {
                mutableAdapterItemsLiveData.postValue(newItems)
            } else {
                mutableAdapterItemsLiveData.value = newItems
            }
        }
    }

    fun back(): Boolean {
        appRouter.exit()
        return true
    }

    fun openProfile(id: Int) {
        appRouter.navigateTo(profileScreen(id))
    }

    fun loadNewPage() {
        if (offlineModeProvider.isInOfflineMode) {
            return
        }
        if (!isLoading) {
            if (hasSearchQuery) {
                searchWorkers(searchQuery, true)
            } else {
                loadWorkers(true)
            }
        }
    }

    fun setSearch(query: String) {
        if (offlineModeProvider.isInOfflineMode) {
            searchOffline(query)
            return
        }
        val newQuery = query.trim()
        if (newQuery.isBlank()) {
            if (hasSearchQuery) {
                _adapterSearchItems.postValue(emptyList())
                searchQuery = null
                searchWorkers(searchQuery, false)
            }
        } else {
            if (newQuery != searchQuery) {
                searchQuery = newQuery
                searchWorkers(searchQuery, false)
            }
        }
    }

    private fun searchOffline(query: String) {
        val newQuery = query.trim()
        if (newQuery.isEmpty()) {
            _adapterSearchItems.value = emptyList()
            searchQuery = null
            return
        }
        searchQuery = newQuery
        val newList = _adapterItems.value.orEmpty()
            .filter { it is WorkerUI && (it.firstName.contains(query) || it.lastName.contains(query)) }
        if (newList.isEmpty()) {
            _adapterSearchItems.value = listOf(EmptySearchResultItem())
        } else {
            _adapterSearchItems.value = newList
        }
    }

    fun load() {
        _isInOfflineLiveData.value = offlineModeProvider.isInOfflineMode
        if (isInOffline) {
            loadWorkersOffline()
        } else {
            loadWorkers(false)
        }
    }

    private fun loadWorkersOffline() {
        updateIsLoading(
            isLoading = true,
            post = true,
            mutableAdapterItemsLiveData = _adapterItems,
        )
        getWorkersOfflineUseCase()
            .map { users -> users.map(mapper::mapProfileToWorker) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { workers ->
                    if (workers.isEmpty()) {
                        _adapterItems.value = listOf(EmptySearchResultItem())
                    } else {
                        _adapterItems.value = workers
                    }
                }, {
                    Timber.e(it)
                }
            )
    }
}
