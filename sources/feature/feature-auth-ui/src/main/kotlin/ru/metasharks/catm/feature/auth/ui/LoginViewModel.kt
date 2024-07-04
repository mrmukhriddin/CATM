package ru.metasharks.catm.feature.auth.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.api.auth.usecase.LoginUseCase
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.AboutAppScreen
import ru.metasharks.catm.core.navigation.screens.AgreeDataScreen
import ru.metasharks.catm.core.navigation.screens.CoreScreen
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import ru.metasharks.catm.core.network.switchurl.BaseUrlSwitcher
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog.Companion.TAG
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.license.UpdateLicenseUseCase
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getAndSaveAndSaveCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateLicenseUseCase: UpdateLicenseUseCase,
    private val appRouter: ApplicationRouter,
    private val coreScreen: CoreScreen,
    private val aboutAppScreen: AboutAppScreen,
    private val agreeDataScreen: AgreeDataScreen,
    private val baseUrlSwitcher: BaseUrlSwitcher,
    private val preferencesProvider: PreferencesProvider,
    @ApplicationContext context: Context
) : ViewModel() {

    private var hasUnreadMessages: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        context.getString(ru.metasharks.catm.core.storage.R.string.preferences_key_has_unread_notifications),
        false
    )

    private val firstLogin: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        "pref.first_login",
        false
    )

    private val compositeDisposable = CompositeDisposable()

    private val _setupUi = MutableLiveData<Boolean>()
    val setupUi: LiveData<Boolean> = _setupUi

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _errorDetail = MutableLiveData<String>()
    val errorDetail: LiveData<String> = _errorDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        _isLoading.value = true
        loginUseCase(username, password)
            .andThen(getAndSaveAndSaveCurrentUserUseCase(true))
            .flatMap { updateLicenseUseCase() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doFinally { _isLoading.value = false }
            .subscribe(
                {

                    if (firstLogin) {
                        appRouter.navigateTo(agreeDataScreen())
                    } else {
                        appRouter.newRootScreen(coreScreen())
                    }

                },
                { error ->
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

    fun openAboutAppScreen() {
        appRouter.navigateTo(aboutAppScreen())
    }

    fun exit() {
        appRouter.exit()
    }

    fun switchBaseUrl(): Boolean {
        return baseUrlSwitcher.switchBaseUrl()
    }

    fun currentUrlState(): Boolean {
        return baseUrlSwitcher.isDemo()
    }

    fun checkIfAlreadyLogged(isUserSession: Boolean?) {
        if (isUserSession == true) {
            appRouter.newRootScreen(coreScreen())
        } else {
            hasUnreadMessages = false
            _setupUi.value = true
        }
    }
}
