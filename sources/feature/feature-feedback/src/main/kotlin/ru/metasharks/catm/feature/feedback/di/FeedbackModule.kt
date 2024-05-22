package ru.metasharks.catm.feature.feedback.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.metasharks.catm.feature.feedback.service.FeedbackApi
import ru.metasharks.catm.feature.feedback.usecase.SendFeedbackUseCase
import ru.metasharks.catm.feature.feedback.usecase.SendFeedbackUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeedbackModule private constructor() {

    @Binds
    abstract fun bindSendFeedbackUseCase(impl: SendFeedbackUseCaseImpl): SendFeedbackUseCase

    companion object {

        @Provides
        fun provideWorkPermitsApi(retrofit: Retrofit): FeedbackApi {
            return retrofit.create(FeedbackApi::class.java)
        }
    }
}
