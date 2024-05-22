package ru.metasharks.catm.feature.qr.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.ProfileScreen
import ru.metasharks.catm.feature.profile.usecase.GetUserByIdUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class QrScannerViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val profileScreen: ProfileScreen,
    private val appRouter: ApplicationRouter,
) : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    fun tryToOpen(id: Int) {
        getUserByIdUseCase(id, fromDb = false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { profile ->
                    appRouter.replaceScreen(profileScreen(id))
                }, { error ->
                    _error.postValue(error)
                    Timber.e(error)
                }
            )
    }

    fun exit() {
        appRouter.exit()
    }
}
