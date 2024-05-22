package ru.metasharks.catm.feature.workpermit.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsScreen
import ru.metasharks.catm.core.navigation.screens.result.CreateWorkPermitScreenForResult
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.getItemsWithLoadingState
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.profile.role.RoleProvider
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData.Companion.isSameTo
import ru.metasharks.catm.feature.workpermit.ui.mapper.StatusesMapper
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetStatusesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsFromDbUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class WorkPermitsViewModel @Inject constructor(
    private val getWorkPermitsUseCase: GetWorkPermitsUseCase,
    private val getWorkPermitsFromDbUseCase: GetWorkPermitsFromDbUseCase,
    private val getStatusesUseCase: GetStatusesUseCase,
    private val workPermitsMapper: WorkPermitsMapper,
    private val statusesMapper: StatusesMapper,
    private val roleProvider: RoleProvider,
    private val appRouter: ApplicationRouter,
    private val createWorkPermitScreenForResult: CreateWorkPermitScreenForResult,
    private val workPermitDetailsScreen: WorkPermitDetailsScreen,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
) : ViewModel() {

    private val _role = MutableLiveData<Role>()
    val role: LiveData<Role> = _role

    var filterData: FilterData? = null

    private var loadWorkPermitsDisposable: Disposable? = null

    private var currentStatus: String? = null
    private val requireCurrentStatus: String
        get() = requireNotNull(currentStatus)

    private var isLoading = false

    private val _isInOfflineLiveData = MutableLiveData<Boolean>()
    val isInOfflineLiveData: LiveData<Boolean> = _isInOfflineLiveData

    private val isInOffline: Boolean
        get() = isInOfflineLiveData.value ?: false

    private val statusesToWorkPermits: MutableMap<String, List<BaseListItem>> = hashMapOf()
    private var currentWorkPermits: List<BaseListItem>?
        get() {
            if (currentStatus == null) {
                return emptyList()
            }
            return statusesToWorkPermits[requireCurrentStatus]
        }
        set(value) {
            if (currentStatus == null) {
                return
            }
            if (value == null) {
                throw IllegalArgumentException("you should not insert null list to hashMap. insert empty list instead")
            }
            statusesToWorkPermits[requireCurrentStatus] = value
        }

    private val statusesToNextPageUrls: MutableMap<String, String?> = hashMapOf()
    private var nextPageUrl: String?
        get() {
            if (currentStatus == null) {
                return null
            }
            return statusesToNextPageUrls[requireCurrentStatus]
        }
        set(value) {
            if (currentStatus == null) {
                return
            }
            statusesToNextPageUrls[requireCurrentStatus] = value
        }

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _statuses = MutableLiveData<List<StatusUiItem>>()
    val statuses: LiveData<List<StatusUiItem>> = _statuses

    private val _workPermits = MutableLiveData<List<BaseListItem>>()
    val workPermits: LiveData<List<BaseListItem>> = _workPermits

    fun init() {
        _isInOfflineLiveData.value = offlineModeProvider.isInOfflineMode
        Single.fromCallable {
            _role.postValue(roleProvider.currentRole)
        }.flatMap {
            getStatusesUseCase(fromDb = isInOffline)
        }
            .map(statusesMapper::mapStatuses)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { statusUiItems ->
                    _statuses.value = statusUiItems
                }, {
                    _error.value = it
                }
            )
    }

    private fun loadWorkPermitsOffline(status: String) {
        Singles.zip(
            pendingActionsRepository.getPayloads(PendingActionPayload.CreateWorkPermit::class),
            getWorkPermitsFromDbUseCase()
        )
            .map { (pending, saved) ->
                if (status == StatusCode.ALL.code) {
                    pending.map { workPermitsMapper.mapWorkPermitPending(it) }
                } else {
                    emptyList()
                } + workPermitsMapper.mapWorkPermits(saved)
                    .filterIsInstance<WorkPermitUi>()
                    .sortedByDescending { it.permitId }
                    .filter {
                        if (status == StatusCode.ALL.code) {
                            true
                        } else {
                            it.statusCode.code == status
                        }
                    }
                    .filter { it.statusCode != StatusCode.ARCHIVED } // do not show archived
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { workPermits ->
                    _workPermits.value = workPermits
                }, { error ->
                    _error.value = error
                }
            )
    }

    fun loadWorkPermits(filterData: FilterData?) {
        if (this.filterData.isSameTo(filterData)) {
            return
        }
        this.filterData = filterData
        statusesToNextPageUrls.clear()
        statusesToWorkPermits.clear()
        _workPermits.value = emptyList()
        loadWorkPermits(false)
    }

    fun loadWorkPermits(status: String, loadNewPage: Boolean) {
        if (isInOffline) {
            if (currentStatus == status) {
                return
            }
            currentStatus = status
            loadWorkPermitsOffline(status)
            return
        }
        if (currentStatus != status) {
            if (isLoading && checkDisposableIsBusy(loadWorkPermitsDisposable)) {
                requireNotNull(loadWorkPermitsDisposable).dispose()
                updateIsLoading(_workPermits, isLoading = false, post = false)
            }
            currentStatus = status
            if (!currentWorkPermits.isNullOrEmpty()) {
                _workPermits.value = currentWorkPermits
                return
            }
        } else if (!loadNewPage) {
            throw IllegalStateException("status is equal to previous status but not loadNewPage")
        }
        loadWorkPermits(loadNewPage)
    }

    fun loadWorkPermits(loadNewPage: Boolean) {
        if (isLoading || loadNewPage && nextPageUrl == null) {
            return
        }
        if (!loadNewPage) {
            currentWorkPermits = emptyList()
            _workPermits.value = emptyList()
        }
        updateIsLoading(_workPermits, isLoading = true, post = true)
        loadWorkPermitsDisposable = if (loadNewPage) {
            getWorkPermitsUseCase(requireCurrentStatus, nextPageUrl, filterData)
        } else {
            getWorkPermitsUseCase(requireCurrentStatus, null, filterData)
        }
            .map { workPermitsMapper.mapWorkPermits(it.results) to it.next }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                updateIsLoading(_workPermits, isLoading = false, post = false)
            }
            .subscribe(
                { (workPermits, next) ->
                    setLoadedWorkPermits(workPermits, next, _workPermits, loadNewPage)
                }, {
                    _error.postValue(it)
                    Timber.e(it)
                }
            )
    }

    private fun setLoadedWorkPermits(
        workPermits: List<BaseListItem>,
        nextPageUrl: String?,
        mutableAdapterItemsLiveData: MutableLiveData<List<BaseListItem>>,
        newPage: Boolean,
    ) {
        this.nextPageUrl = nextPageUrl
        val currentList = mutableAdapterItemsLiveData.value ?: emptyList()
        val finalList = if (newPage) {
            currentList.plus(workPermits)
        } else {
            workPermits
        }
        currentWorkPermits = finalList
        mutableAdapterItemsLiveData.value = finalList
    }

    private fun updateIsLoading(
        mutableAdapterItemsLiveData: MutableLiveData<List<BaseListItem>>,
        isLoading: Boolean,
        post: Boolean,
    ) {
        this.isLoading = isLoading
        val currentItems = mutableAdapterItemsLiveData.value ?: emptyList()
        val (newItems, changed) = getItemsWithLoadingState(currentItems, isLoading)

        if (changed) {
            currentWorkPermits = newItems
            if (post) {
                mutableAdapterItemsLiveData.postValue(newItems)
            } else {
                mutableAdapterItemsLiveData.value = newItems
            }
        }
    }

    fun openCreateWorkPermitScreen() {
        appRouter.navigateToWithResult<Boolean>(createWorkPermitScreenForResult()) { result ->
            if (result) {
                if (isInOffline) {
                    loadWorkPermitsOffline(requireCurrentStatus)
                } else {
                    loadWorkPermits(false)
                }
            }
        }
    }

    fun openWorkPermit(workPermitId: Long) {
        appRouter.navigateToWithResult<Boolean>(workPermitDetailsScreen(workPermitId)) { result ->
            if (result) {
                loadWorkPermits(false)
            }
        }
    }

    private fun checkDisposableIsBusy(disposable: Disposable?): Boolean {
        return disposable != null && !disposable.isDisposed
    }

    fun exit() {
        appRouter.exit()
    }
}
