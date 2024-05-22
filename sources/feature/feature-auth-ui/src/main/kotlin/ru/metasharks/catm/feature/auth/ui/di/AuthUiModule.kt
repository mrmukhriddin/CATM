package ru.metasharks.catm.feature.auth.ui.di

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.AuthAfterTokenExpiredScreen
import ru.metasharks.catm.core.navigation.screens.AuthScreen
import ru.metasharks.catm.core.navigation.screens.LoginIntentRouter
import ru.metasharks.catm.feature.auth.ui.LoginActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthUiModule private constructor() {

    companion object {

        /**
         * В рамках исключения здесь мы не используем абстракцию Screen из Cicerone
         * Данное исключение было допущено ввиду проблемы при запуске скрина со SplashActivity
         */
        @Provides
        fun provideLoginIntentRouter(): LoginIntentRouter =
            LoginIntentRouter { context, isUserSession ->
                LoginActivity.createIntent(context, isUserSession)
            }

        @Provides
        fun provideAuthScreen(): AuthScreen =
            AuthScreen {
                ActivityScreen { context ->
                    Intent(context, LoginActivity::class.java)
                }
            }

        @Provides
        fun provideAuthAfterTokenExpiredScreen(): AuthAfterTokenExpiredScreen =
            AuthAfterTokenExpiredScreen {
                ActivityScreen { context ->
                    LoginActivity.createIntentAfterTokenExpired(context)
                }
            }
    }
}
