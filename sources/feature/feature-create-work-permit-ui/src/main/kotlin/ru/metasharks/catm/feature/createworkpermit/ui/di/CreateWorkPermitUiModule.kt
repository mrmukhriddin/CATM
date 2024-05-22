package ru.metasharks.catm.feature.createworkpermit.ui.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.result.CreateWorkPermitScreenForResult
import ru.metasharks.catm.core.navigation.screens.result.ResultScreen
import ru.metasharks.catm.feature.createworkpermit.ui.CreateWorkPermitActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CreateWorkPermitUiModule private constructor() {

    companion object {

        @Provides
        fun provideCreateWorkPermitScreenForResult(): CreateWorkPermitScreenForResult {
            return CreateWorkPermitScreenForResult {
                object : ResultScreen {

                    override val resultKey: String = CreateWorkPermitScreenForResult.KEY

                    override fun createIntent(context: Context): Intent {
                        return Intent(context, CreateWorkPermitActivity::class.java)
                    }
                }
            }
        }
    }
}
