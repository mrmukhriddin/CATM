package ru.metasharks.catm.feature.briefings.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.network.ApiClient
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingCategoriesUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingCategoriesUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingDetailsUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingDetailsUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingTypesUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingTypesUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingsUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingsUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.GetMainBriefingDataUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetMainBriefingDataUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.GetQuizUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetQuizUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.PassQuizUseCase
import ru.metasharks.catm.feature.briefings.usecase.PassQuizUseCaseImpl
import ru.metasharks.catm.feature.briefings.usecase.SignBriefingUseCase
import ru.metasharks.catm.feature.briefings.usecase.SignBriefingUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BriefingsModule private constructor() {

    @Binds
    abstract fun bindGetBriefingCategoriesUseCase(impl: GetBriefingCategoriesUseCaseImpl): GetBriefingCategoriesUseCase

    @Binds
    abstract fun bindGetBriefingsUseCase(impl: GetBriefingsUseCaseImpl): GetBriefingsUseCase

    @Binds
    abstract fun bindGetBriefingDetailsUseCase(impl: GetBriefingDetailsUseCaseImpl): GetBriefingDetailsUseCase

    @Binds
    abstract fun bindGetBriefingTypesUseCase(impl: GetBriefingTypesUseCaseImpl): GetBriefingTypesUseCase

    @Binds
    abstract fun bindGetMainBriefingDataUseCase(impl: GetMainBriefingDataUseCaseImpl): GetMainBriefingDataUseCase

    @Binds
    abstract fun bindGetQuizUseCase(impl: GetQuizUseCaseImpl): GetQuizUseCase

    @Binds
    abstract fun bindPassQuizUseCase(impl: PassQuizUseCaseImpl): PassQuizUseCase

    @Binds
    abstract fun bindSignBriefingUseCase(impl: SignBriefingUseCaseImpl): SignBriefingUseCase

    companion object {

        @Provides
        fun provideDocumentsApi(client: ApiClient): BriefingsApi {
            return client.createService(BriefingsApi::class.java)
        }
    }
}
