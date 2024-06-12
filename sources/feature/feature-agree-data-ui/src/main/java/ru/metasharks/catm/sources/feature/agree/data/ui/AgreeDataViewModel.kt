package ru.metasharks.catm.sources.feature.agree.data.ui;

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import ru.metasharks.catm.api.auth.usecase.VerifySuffixUseCase
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.CoreScreen
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.feature.agree.data.ui.R
import javax.inject.Inject

@HiltViewModel
class AgreeDataViewModel @Inject constructor(
    private val verifySuffixUseCase: VerifySuffixUseCase,
    private val appRouter: ApplicationRouter,
    private val coreScreen: CoreScreen,
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _errorDetail = MutableLiveData<String?>()
    val errorDetail: MutableLiveData<String?> = _errorDetail

    private val _verificationSuccess = MutableLiveData<Boolean>()
    val verificationSuccess: LiveData<Boolean> = _verificationSuccess

    fun verifySuffix(suffix: String) {
        _isLoading.value = true
        verifySuffixUseCase(suffix)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doFinally { _isLoading.value = false }
            .subscribe(
                {
                    _verificationSuccess.value = true
                    appRouter.newRootScreen(coreScreen())
                },
                { error ->
                    _verificationSuccess.value = false
                    val errorMessage = ErrorHandler.getDetailMessageFromHttpException(error)
                    if (errorMessage == null) {
                        _error.value = error
                    } else {
                        _errorDetail.value = errorMessage
                    }
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun exit() {
        appRouter.exit()
    }
}
