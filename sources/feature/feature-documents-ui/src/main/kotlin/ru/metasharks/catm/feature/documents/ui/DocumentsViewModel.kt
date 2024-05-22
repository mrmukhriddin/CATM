package ru.metasharks.catm.feature.documents.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.getItemsWithLoadingState
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentDirectoryUI
import ru.metasharks.catm.feature.documents.ui.recycler.entities.mapper.Mapper
import ru.metasharks.catm.feature.documents.usecase.GetDocumentTypesUseCase
import ru.metasharks.catm.feature.documents.usecase.GetDocumentsUseCase
import ru.metasharks.catm.feature.documents.usecase.SearchDocumentsUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class DocumentsViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val mediaPreviewScreen: MediaPreviewScreen,
    private val getDocumentTypesUseCase: GetDocumentTypesUseCase,
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val searchDocumentsUseCase: SearchDocumentsUseCase,
    private val mapper: Mapper,
) : ViewModel() {

    private var searchQuery: String? = null
        set(value) {
            field = value
            _inSearch.value = field != null
        }

    private val hasSearchQuery: Boolean
        get() = searchQuery != null

    private var isLoading: Boolean = false

    private var loadDocumentsDisposable: Disposable? = null
    private lateinit var currentDirectoryUI: DocumentDirectoryUI

    private var searchNextUrl: String? = null

    private val previousDirectoryUI: DocumentDirectoryUI?
        get() = currentDirectoryUI.previousDirectoryUI

    private val _adapterItems = MutableLiveData<List<BaseListItem>>()
    val adapterItems: LiveData<List<BaseListItem>> = _adapterItems

    private val _adapterSearchItems = MutableLiveData<List<BaseListItem>>()
    val adapterSearchItems: LiveData<List<BaseListItem>> = _adapterSearchItems

    private val _currentDirectoryId = MutableLiveData(MAIN)
    val currentDirectoryId: LiveData<Int> = _currentDirectoryId

    private val _currentDirectoryName = MutableLiveData<String>()
    val currentDirectoryName: LiveData<String> = _currentDirectoryName

    private val _inSearch = MutableLiveData<Boolean>()
    val inSearch: LiveData<Boolean> = _inSearch

    private val _loadingSearch = MutableLiveData<Boolean>()
    val loadingSearch: LiveData<Boolean> = _loadingSearch

    fun loadDocumentDirectories(mainDirectoryName: String) {
        if (isLoading) {
            return
        }
        isLoading = true
        getDocumentTypesUseCase()
            .map { it.map(mapper::mapDocumentType) }
            .map { documentDirectories ->
                DocumentDirectoryUI(
                    typeId = MAIN,
                    name = mainDirectoryName,
                    listDocuments = documentDirectories
                )
            }
            .doFinally { isLoading = false }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { mainDocumentDirectory ->
                    currentDirectoryUI = mainDocumentDirectory
                    _adapterItems.value =
                        currentDirectoryUI.listDocuments ?: emptyList()
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    fun displayPrevious() {
        if (previousDirectoryUI == null) {
            return
        }
        setClickedDocumentDirectory(requireNotNull(previousDirectoryUI), false)
    }

    fun loadDocumentsForType(typeId: Int, loadNextPage: Boolean) {
        if (isLoading || loadNextPage && currentDirectoryUI.nextPageUrl == null || checkDisposableIsBusy(
                loadDocumentsDisposable
            )
        ) {
            return
        }
        val processingDocumentDirectory = if (loadNextPage) {
            currentDirectoryUI
        } else {
            currentDirectoryUI.listDocuments?.firstOrNull {
                it is DocumentDirectoryUI && it.typeId == typeId
            } as DocumentDirectoryUI? ?: return
        }
        if (processingDocumentDirectory.listDocuments != null && !loadNextPage) {
            setClickedDocumentDirectory(processingDocumentDirectory, false)
            return
        }
        updateIsLoading(_adapterItems, isLoading = true, post = true)
        loadDocumentsDisposable =
            if (loadNextPage) {
                getDocumentsUseCase(typeId, requireNotNull(currentDirectoryUI.nextPageUrl))
            } else {
                getDocumentsUseCase(typeId, null)
            }
                .map { it.documents.map(mapper::mapDocument) to it.next }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    updateIsLoading(_adapterItems, isLoading = false, post = false)
                    requireNotNull(loadDocumentsDisposable).dispose()
                }
                .subscribe(
                    { (newDocuments, nextUrl) ->
                        processingDocumentDirectory.nextPageUrl = nextUrl
                        if (loadNextPage) {
                            processingDocumentDirectory.listDocuments =
                                requireNotNull(processingDocumentDirectory.listDocuments).plus(
                                    newDocuments
                                )
                            setClickedDocumentDirectory(processingDocumentDirectory, true)
                        } else {
                            processingDocumentDirectory.listDocuments = newDocuments
                            processingDocumentDirectory.previousDirectoryUI = currentDirectoryUI
                            setClickedDocumentDirectory(processingDocumentDirectory, false)
                        }
                    }, { error ->
                        Timber.e(error)
                    }
                )
    }

    private fun searchDocuments(typeId: Int, loadNextPage: Boolean, query: String?) {
        if (query == null) {
            _adapterSearchItems.postValue(emptyList())
            return
        }
        if (loadNextPage && searchNextUrl == null) {
            return
        }
        updateIsLoading(_adapterSearchItems, isLoading = true, post = true, search = true)
        if (loadNextPage) {
            searchDocumentsUseCase(typeId, query, searchNextUrl)
        } else {
            searchDocumentsUseCase(typeId, query, null)
        }
            .map { it.documents.map(mapper::mapDocument) to it.next }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                updateIsLoading(_adapterSearchItems, isLoading = false, post = false, search = true)
                requireNotNull(loadDocumentsDisposable).dispose()
            }
            .subscribe(
                { (newDocuments, nextUrl) ->
                    if (loadNextPage) {
                        _adapterSearchItems.postValue(
                            (_adapterSearchItems.value ?: emptyList()).plus(newDocuments)
                        )
                    } else {
                        _adapterSearchItems.postValue(newDocuments)
                    }
                    searchNextUrl = nextUrl
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    private fun setClickedDocumentDirectory(document: DocumentDirectoryUI, newPage: Boolean) {
        if (!newPage) {
            currentDirectoryUI = document
            _currentDirectoryId.value = document.typeId
            _currentDirectoryName.value = document.name
        }
        _adapterItems.value = requireNotNull(document.listDocuments)
    }

    private fun checkDisposableIsBusy(disposable: Disposable?): Boolean {
        return disposable != null && !disposable.isDisposed
    }

    private fun updateIsLoading(
        mutableAdapterItemsLiveData: MutableLiveData<List<BaseListItem>>,
        isLoading: Boolean,
        post: Boolean,
        search: Boolean = false,
    ) {
        this.isLoading = isLoading
        val currentItems = mutableAdapterItemsLiveData.value ?: emptyList()

        if (search) {
            _loadingSearch.postValue(isLoading)
            return
        }

        val (newItems, changed) = getItemsWithLoadingState(currentItems, isLoading)
        if (changed) {
            if (post) {
                mutableAdapterItemsLiveData.postValue(newItems)
            } else {
                mutableAdapterItemsLiveData.value = newItems
            }
        }
    }

    fun openFile(fileUri: String) {
        appRouter.navigateTo(mediaPreviewScreen(fileUri, null))
    }

    fun loadNewPage() {
        if (!isLoading) {
            val currentId = requireNotNull(currentDirectoryId.value)
            if (hasSearchQuery) {
                searchDocuments(currentId, true, searchQuery)
            } else {
                loadDocumentsForType(currentId, true)
            }
        }
    }

    fun setNewSearchQuery(searchQuery: String) {
        val newQuery = searchQuery.trim()
        if (newQuery.isBlank()) {
            if (hasSearchQuery) {
                this.searchQuery = null
                searchDocuments(requireNotNull(currentDirectoryId.value), false, null)
            }
        } else {
            if (newQuery != this.searchQuery) {
                this.searchQuery = newQuery
                searchDocuments(requireNotNull(currentDirectoryId.value), false, newQuery)
            }
        }
    }

    companion object {

        const val MAIN = -1
    }
}
