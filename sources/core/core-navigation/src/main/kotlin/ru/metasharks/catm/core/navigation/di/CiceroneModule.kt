package ru.metasharks.catm.core.navigation.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.ApplicationNavigator
import ru.metasharks.catm.core.navigation.ApplicationRouter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CiceroneModule private constructor() {

    @Binds
    abstract fun bindRouter(impl: ApplicationRouter): Router

    companion object {

        @Provides
        @Singleton
        fun provideApplicationNavigator(): ApplicationNavigator {
            return ApplicationNavigator()
        }

        @Provides
        @Singleton
        fun provideAppCicerone(applicationNavigator: ApplicationNavigator): Cicerone<ApplicationRouter> {
            val cicerone = Cicerone.create(ApplicationRouter())
            cicerone.getNavigatorHolder().setNavigator(applicationNavigator)
            return cicerone
        }

        @Provides
        @Singleton
        fun provideNavigationHolder(cicerone: Cicerone<ApplicationRouter>): NavigatorHolder =
            cicerone.getNavigatorHolder()

        @Provides
        @Singleton
        fun provideRouter(cicerone: Cicerone<ApplicationRouter>) = cicerone.router
    }
}
