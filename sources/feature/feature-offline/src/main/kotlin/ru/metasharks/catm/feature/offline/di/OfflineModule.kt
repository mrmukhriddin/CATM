package ru.metasharks.catm.feature.offline.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.metasharks.catm.feature.offline.pending.usecases.GetPendingActionPayloadsUseCase
import ru.metasharks.catm.feature.offline.pending.usecases.GetPendingActionsPayloadsUseCaseImpl
import ru.metasharks.catm.feature.offline.pending.usecases.SavePendingActionUseCase
import ru.metasharks.catm.feature.offline.pending.usecases.SavePendingActionUseCaseImpl
import ru.metasharks.catm.feature.offline.save.DownloadFileUseCase
import ru.metasharks.catm.feature.offline.save.DownloadFileUseCaseImpl
import ru.metasharks.catm.feature.offline.save.briefings.GetBriefingOfflineDataUseCase
import ru.metasharks.catm.feature.offline.save.briefings.GetBriefingOfflineDataUseCaseImpl
import ru.metasharks.catm.feature.offline.save.briefings.SaveBriefingsUseCase
import ru.metasharks.catm.feature.offline.save.briefings.SaveBriefingsUseCaseImpl
import ru.metasharks.catm.feature.offline.save.clear.ClearAllUseCase
import ru.metasharks.catm.feature.offline.save.clear.ClearAllUseCaseImpl
import ru.metasharks.catm.feature.offline.save.clear.ClearPendingRequestsUseCase
import ru.metasharks.catm.feature.offline.save.clear.ClearPendingRequestsUseCaseImpl
import ru.metasharks.catm.feature.offline.save.profile.SaveProfileUseCase
import ru.metasharks.catm.feature.offline.save.profile.SaveProfileUseCaseImpl
import ru.metasharks.catm.feature.offline.save.profile.SaveWorkersUseCase
import ru.metasharks.catm.feature.offline.save.profile.SaveWorkersUseCaseImpl
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCaseImpl
import ru.metasharks.catm.feature.offline.save.workpermit.SaveWorkPermitsUseCase
import ru.metasharks.catm.feature.offline.save.workpermit.SaveWorkPermitsUseCaseImpl
import ru.metasharks.catm.feature.offline.service.OfflineApi
import ru.metasharks.catm.feature.offline.usecases.DeleteOfflineUseCase
import ru.metasharks.catm.feature.offline.usecases.DeleteOfflineUseCaseImpl
import ru.metasharks.catm.feature.offline.usecases.GetSavedOfflineUseCase
import ru.metasharks.catm.feature.offline.usecases.GetSavedOfflineUseCaseImpl
import ru.metasharks.catm.feature.offline.usecases.SaveOfflineUseCase
import ru.metasharks.catm.feature.offline.usecases.SaveOfflineUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OfflineModule private constructor() {

    @Binds
    abstract fun bindDownloadFileUseCase(impl: DownloadFileUseCaseImpl): DownloadFileUseCase

    @Binds
    abstract fun bindSaveProfileUseCase(impl: SaveProfileUseCaseImpl): SaveProfileUseCase

    @Binds
    abstract fun bindSaveOfflineUseCase(impl: SaveOfflineUseCaseImpl): SaveOfflineUseCase

    @Binds
    abstract fun bindGetSavedOfflineUseCase(impl: GetSavedOfflineUseCaseImpl): GetSavedOfflineUseCase

    @Binds
    abstract fun bindSaveWorkersUseCase(impl: SaveWorkersUseCaseImpl): SaveWorkersUseCase

    @Binds
    abstract fun bindSaveWorkPermitsUseCase(impl: SaveWorkPermitsUseCaseImpl): SaveWorkPermitsUseCase

    @Binds
    abstract fun bindSaveBriefingsUseCase(impl: SaveBriefingsUseCaseImpl): SaveBriefingsUseCase

    @Binds
    abstract fun bindGetBriefingOfflineDataUseCase(impl: GetBriefingOfflineDataUseCaseImpl): GetBriefingOfflineDataUseCase

    @Binds
    abstract fun bindGetWorkPermitCreateOfflineToolsUseCase(impl: GetWorkPermitCreateOfflineToolsUseCaseImpl): GetWorkPermitCreateOfflineToolsUseCase

    @Binds
    abstract fun bindSavePendingActionUseCase(impl: SavePendingActionUseCaseImpl): SavePendingActionUseCase

    @Binds
    abstract fun bindGetPendingActionUseCase(impl: GetPendingActionsPayloadsUseCaseImpl): GetPendingActionPayloadsUseCase

    @Binds
    abstract fun bindClearPendingRequestsUseCase(impl: ClearPendingRequestsUseCaseImpl): ClearPendingRequestsUseCase

    @Binds
    abstract fun bindClearAllUseCase(impl: ClearAllUseCaseImpl): ClearAllUseCase

    @Binds
    abstract fun bindDeleteOfflineUseCase(impl: DeleteOfflineUseCaseImpl): DeleteOfflineUseCase

    companion object {

        @Provides
        fun provideOfflineApi(retrofit: Retrofit): OfflineApi {
            return retrofit.create(OfflineApi::class.java)
        }
    }
}
