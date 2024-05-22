package ru.metasharks.catm.feature.workpermit.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal open class WorkPermitFromDbViewModel @Inject constructor(
    protected val getWorkPermitUseCase: GetWorkPermitUseCase,
    protected val getCurrentUserUseCase: GetCurrentUserUseCase,
    protected val mapper: WorkPermitsMapper,
    protected open val appRouter: ApplicationRouter,
) : ViewModel() {

    protected var workPermitId: Long = -1L

    private val _workPermit = MutableLiveData<WorkPermitDetailsUi>()
    val workPermit: LiveData<WorkPermitDetailsUi> = _workPermit

    fun load(workPermitId: Long, forceRefresh: Boolean = false) {
        Singles.zip(
            getWorkPermitUseCase(workPermitId, forceRefresh),
            getCurrentUserUseCase(false)
        )
            .map { (workPermit, user) ->
                mapper.mapWorkPermit(workPermit, user)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    this.workPermitId = workPermitId
                    _workPermit.value = it
                    onWorkPermitSet(it)
                }, {
                    Timber.e(it)
                }
            )
    }

    protected open fun onWorkPermitSet(workPermit: WorkPermitDetailsUi) = Unit

    open fun exit() {
        appRouter.exit()
    }
}
