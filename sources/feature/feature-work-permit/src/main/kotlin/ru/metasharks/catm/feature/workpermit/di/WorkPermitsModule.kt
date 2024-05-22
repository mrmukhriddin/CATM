package ru.metasharks.catm.feature.workpermit.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import ru.metasharks.catm.feature.workpermit.usecase.DeleteWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.DeleteWorkPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetOptionsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetOptionsUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleEmployeesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleEmployeesUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleManagersUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleManagersUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetStatusesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetStatusesUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsFromDbUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsFromDbUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkTypesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkTypesUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.create.CreateWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.CreateWorkPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.DeleteAdditionalDocumentUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.DeleteAdditionalDocumentUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.GetRiskFactorsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.GetRiskFactorsUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.UploadAdditionalDocumentUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.UploadAdditionalDocumentUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.CreateDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.CreateDailyPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.DeleteDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.DeleteDailyPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.SignDailyPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit.SignDailyPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.AddGasAirAnalysisUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.AddGasAirAnalysisUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.DeleteGasAirAnalysisUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.gasairanalysis.DeleteGasAirAnalysisUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.CloseWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.CloseWorkPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.ExtendWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.ExtendWorkPermitUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.SignExtensionUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.signed.SignExtensionUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.SignAllWorkersBriefingUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.SignAllWorkersBriefingUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.UpdateWorkersUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.workers.UpdateWorkersUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.sign.CheckIfAllowedSignUseCase
import ru.metasharks.catm.feature.workpermit.usecase.sign.CheckIfAllowedSignUseCaseImpl
import ru.metasharks.catm.feature.workpermit.usecase.sign.SignWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.sign.SignWorkPermitUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class WorkPermitsModule private constructor() {

    @Binds
    abstract fun bindGetWorkPermitsUseCase(impl: GetWorkPermitsUseCaseImpl): GetWorkPermitsUseCase

    @Binds
    abstract fun bindGetStatusesUseCase(impl: GetStatusesUseCaseImpl): GetStatusesUseCase

    @Binds
    abstract fun bindGetWorkTypesUseCase(impl: GetWorkTypesUseCaseImpl): GetWorkTypesUseCase

    @Binds
    abstract fun bindGetResponsibleManagersUseCase(impl: GetResponsibleManagersUseCaseImpl): GetResponsibleManagersUseCase

    @Binds
    abstract fun bindGetResponsibleEmployeesUseCase(impl: GetResponsibleEmployeesUseCaseImpl): GetResponsibleEmployeesUseCase

    @Binds
    abstract fun bindGetEmployeesUseCase(impl: GetEmployeesUseCaseImpl): GetEmployeesUseCase

    @Binds
    abstract fun bindGetOptionsUseCase(impl: GetOptionsUseCaseImpl): GetOptionsUseCase

    @Binds
    abstract fun bindCreateWorkPermitUseCase(impl: CreateWorkPermitUseCaseImpl): CreateWorkPermitUseCase

    @Binds
    abstract fun bindGetWorkPermitUseCase(impl: GetWorkPermitUseCaseImpl): GetWorkPermitUseCase

    @Binds
    abstract fun bindDeleteWorkPermitUseCase(impl: DeleteWorkPermitUseCaseImpl): DeleteWorkPermitUseCase

    @Binds
    abstract fun bindUploadAdditionalDocumentUseCase(impl: UploadAdditionalDocumentUseCaseImpl): UploadAdditionalDocumentUseCase

    @Binds
    abstract fun bindDeleteAdditionalDocumentUseCase(impl: DeleteAdditionalDocumentUseCaseImpl): DeleteAdditionalDocumentUseCase

    @Binds
    abstract fun bindSignWorkPermitUseCase(impl: SignWorkPermitUseCaseImpl): SignWorkPermitUseCase

    @Binds
    abstract fun bindCheckIfAllowedSignUseCase(impl: CheckIfAllowedSignUseCaseImpl): CheckIfAllowedSignUseCase

    @Binds
    abstract fun bindGenerateWorkPermitPdfUseCase(impl: GenerateWorkPermitPdfUseCaseImpl): GenerateWorkPermitPdfUseCase

    @Binds
    abstract fun bindGetRiskFactorsUseCase(impl: GetRiskFactorsUseCaseImpl): GetRiskFactorsUseCase

    @Binds
    abstract fun bindAddGasAirAnalysisUseCase(impl: AddGasAirAnalysisUseCaseImpl): AddGasAirAnalysisUseCase

    @Binds
    abstract fun bindDeleteGasAirAnalysisUseCase(impl: DeleteGasAirAnalysisUseCaseImpl): DeleteGasAirAnalysisUseCase

    @Binds
    abstract fun bindCloseWorkPermitUseCase(impl: CloseWorkPermitUseCaseImpl): CloseWorkPermitUseCase

    @Binds
    abstract fun bindExtendWorkPermitUseCase(impl: ExtendWorkPermitUseCaseImpl): ExtendWorkPermitUseCase

    @Binds
    abstract fun bindSignExtensionUseCase(impl: SignExtensionUseCaseImpl): SignExtensionUseCase

    @Binds
    abstract fun bindCreateDailyPermitUseCase(impl: CreateDailyPermitUseCaseImpl): CreateDailyPermitUseCase

    @Binds
    abstract fun bindDeleteDailyPermitUseCase(impl: DeleteDailyPermitUseCaseImpl): DeleteDailyPermitUseCase

    @Binds
    abstract fun bindSignDailyPermitUseCase(impl: SignDailyPermitUseCaseImpl): SignDailyPermitUseCase

    @Binds
    abstract fun bindUpdateWorkersUseCase(impl: UpdateWorkersUseCaseImpl): UpdateWorkersUseCase

    @Binds
    abstract fun bindSignAllWorkersBriefingUseCase(impl: SignAllWorkersBriefingUseCaseImpl): SignAllWorkersBriefingUseCase

    @Binds
    abstract fun bindGetWorkPermitsFromDbUseCase(impl: GetWorkPermitsFromDbUseCaseImpl): GetWorkPermitsFromDbUseCase

    companion object {

        @Provides
        fun provideWorkPermitsApi(retrofit: Retrofit): WorkPermitsApi {
            return retrofit.create(WorkPermitsApi::class.java)
        }
    }
}
