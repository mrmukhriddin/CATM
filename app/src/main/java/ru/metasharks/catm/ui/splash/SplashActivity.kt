package ru.metasharks.catm.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.navigation.screens.CoreScreen
import ru.metasharks.catm.core.navigation.screens.LoginIntentRouter
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import ru.metasharks.catm.feature.profile.usecase.license.UpdateLicenseUseCase
import javax.inject.Inject

@AndroidEntryPoint
internal class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var loginIntentRouter: LoginIntentRouter

    @Inject
    lateinit var authTokenStorage: AuthTokenStorage

    @Inject
    lateinit var updateLicenseUseCase: UpdateLicenseUseCase

    @Inject
    lateinit var coreScreen: CoreScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isAuthorized: Boolean = authTokenStorage.isValid
        if (isAuthorized) {
            updateLicenseUseCase().onErrorComplete().blockingGet()
        }
        startActivity(loginIntentRouter.createIntent(this, isAuthorized))
        finish()
    }
}
