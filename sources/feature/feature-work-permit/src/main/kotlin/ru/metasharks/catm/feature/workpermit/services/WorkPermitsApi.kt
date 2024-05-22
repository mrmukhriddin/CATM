package ru.metasharks.catm.feature.workpermit.services

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitsEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX
import ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit.CreateDailyPermitResponseX
import ru.metasharks.catm.feature.workpermit.entities.certain.gasairanalysis.AddGasAirAnalysisEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.certain.upload.UploadAdditionalDocumentResponseX
import ru.metasharks.catm.feature.workpermit.entities.certain.workers.UpdateWorkersRequestX
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitResponseX
import ru.metasharks.catm.feature.workpermit.entities.options.OptionsEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUsersEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.riskfactors.RiskFactorStageX
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusesEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.worktype.WorkTypeX

interface WorkPermitsApi {

    @GET
    @Headers(RequestModifiers.AUTH)
    fun getWorkPermitsByUrl(
        @Url url: String
    ): Single<WorkPermitsEnvelopeX>

    @GET("work-permits")
    @Headers(RequestModifiers.AUTH)
    fun getWorkPermits(
        @Query("created_after") createdAfter: String? = null,
        @Query("created_before") createdBefore: String? = null,
        @Query("responsible_manager_id") responsibleManagerId: Long? = null,
        @Query("work_type_id") workTypeId: Long? = null,
        @Query("status") status: String? = null,
        @Query("ordering") order: String = "-created",
    ): Single<WorkPermitsEnvelopeX>

    @GET("work-permits/statuses")
    @Headers(RequestModifiers.AUTH)
    fun getStatuses(): Single<StatusesEnvelopeX>

    @GET("work-permits/work-type")
    @Headers(RequestModifiers.AUTH)
    fun getWorkTypes(): Single<List<WorkTypeX>>

    @GET("work-permits/users/responsible_managers")
    @Headers(RequestModifiers.AUTH)
    fun getResponsibleManagers(
        @Query("search") query: String?
    ): Single<WorkPermitUsersEnvelopeX>

    @GET("work-permits/users/employees")
    @Headers(RequestModifiers.AUTH)
    fun getEmployees(@Query("search") query: String?): Single<WorkPermitUsersEnvelopeX>

    @GET("work-permits/users/everyone_except_employees")
    @Headers(RequestModifiers.AUTH)
    fun getPickerListItems(
        @Query("search") query: String?
    ): Single<WorkPermitUsersEnvelopeX>

    @GET
    @Headers(RequestModifiers.AUTH)
    fun getResponsibleManagersByUrl(@Url url: String): Single<WorkPermitUsersEnvelopeX>

    @GET("work-permits/options")
    @Headers(RequestModifiers.AUTH)
    fun getOptions(): Single<OptionsEnvelopeX>

    @Multipart
    @POST("work-permits/")
    @Headers(RequestModifiers.AUTH)
    fun createWorkPermit(
        @Part body: List<MultipartBody.Part>
    ): Single<CreateWorkPermitResponseX>

    @GET("work-permits/{id}")
    @Headers(RequestModifiers.AUTH)
    fun getWorkPermitById(
        @Path("id") workPermitId: Long
    ): Single<WorkPermitDetailsX>

    @DELETE("work-permits/{id}/")
    @Headers(RequestModifiers.AUTH)
    fun deleteWorkPermit(
        @Path("id") workPermitId: Long
    ): Completable

    @Multipart
    @POST("work-permits/{id}/upload_document/")
    @Headers(RequestModifiers.AUTH)
    fun uploadAdditionalDocument(
        @Path("id") workPermitId: Long,
        @Part file: MultipartBody.Part
    ): Single<UploadAdditionalDocumentResponseX>

    @DELETE("work-permits/{id}/{doc_id}")
    @Headers(RequestModifiers.AUTH)
    fun deleteAdditionalDocument(
        @Path("id") workPermitId: Long,
        @Path("doc_id") docToDeleteId: Long,
    ): Completable

    @POST("work-permits/{id}/sign")
    @Headers(RequestModifiers.AUTH)
    fun signWorkPermit(
        @Path("id") workPermitId: Long,
        @Query("role") role: String,
        @Query("sign") sign: Boolean = true,
    ): Completable

    @GET("work-permits/{id}/sign")
    @Headers(RequestModifiers.AUTH)
    fun isAllowedSignWorkPermit(
        @Path("id") workPermitId: Long,
        @Query("role") role: String,
    ): Completable

    @POST("work-permits/{id}/pdf")
    @Headers(RequestModifiers.AUTH)
    fun generateWorkPermitPdf(
        @Path("id") workPermitId: Long
    ): Completable

    @GET("work-permits/risk_factors_document")
    @Headers(RequestModifiers.AUTH)
    fun getRiskFactors(): Single<List<RiskFactorStageX>>

    @POST("work-permits/gas_analysis")
    @Headers(RequestModifiers.AUTH)
    fun addGasAirAnalysis(
        @Body body: AddGasAirAnalysisEnvelopeX,
    ): Single<AddGasAirAnalysisEnvelopeX>

    @DELETE("work-permits/gas_analysis/{id}")
    @Headers(RequestModifiers.AUTH)
    fun deleteGasAirAnalysis(
        @Path("id") workPermitId: Long,
    ): Completable

    @POST("work-permits/{id}/close/")
    @Headers(RequestModifiers.AUTH)
    fun closeWorkPermit(
        @Path("id") workPermitId: Long,
    ): Completable

    @POST("work-permits/{id}/extend/")
    @Multipart
    @Headers(RequestModifiers.AUTH)
    fun extendWorkPermit(
        @Path("id") workPermitId: Long,
        @Part body: List<MultipartBody.Part>,
    ): Completable

    @POST("work-permits/{id}/extend/sign")
    @Headers(RequestModifiers.AUTH)
    fun signExtensionWorkPermit(
        @Path("id") workPermitId: Long,
        @Query("sign") sign: Boolean = true,
    ): Completable

    @POST("work-permits/{id}/add_daily/")
    @Multipart
    @Headers(RequestModifiers.AUTH)
    fun createDailyPermit(
        @Path("id") workPermitId: Long,
        @Part body: List<MultipartBody.Part>
    ): Single<CreateDailyPermitResponseX>

    @POST("work-permits/{work_permit_id}/daily/{daily_id}/sign")
    @Headers(RequestModifiers.AUTH)
    fun signDailyPermit(
        @Path("work_permit_id") workPermitId: Long,
        @Path("daily_id") dailyId: Long,
        @Query("role") role: String,
        @Query("date_end") dateEnd: String? = null
    ): Completable

    @DELETE("work-permits/{work_permit_id}/daily/{daily_id}/sign")
    @Headers(RequestModifiers.AUTH)
    fun deleteDailyPermit(
        @Path("work_permit_id") workPermitId: Long,
        @Path("daily_id") dailyId: Long,
    ): Completable

    @POST("work-permits/{work_permit_id}/update_workers")
    @Headers(RequestModifiers.AUTH)
    fun updateWorkers(
        @Path("work_permit_id") workPermitId: Long,
        @Body body: UpdateWorkersRequestX,
    ): Completable

    @POST("briefings/{briefing_id}/sign_all")
    @Headers(RequestModifiers.AUTH)
    fun signWorkersBriefing(
        @Path("briefing_id") briefingId: Long,
    ): Completable
}
