package ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit

import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit.CreateDailyPermitDataX
import ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit.CreateDailyPermitResponseX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface CreateDailyPermitUseCase {

    operator fun invoke(
        workPermitId: Long,
        request: CreateDailyPermitDataX
    ): Single<CreateDailyPermitResponseX>
}

internal class CreateDailyPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : CreateDailyPermitUseCase {

    override fun invoke(
        workPermitId: Long,
        request: CreateDailyPermitDataX
    ): Single<CreateDailyPermitResponseX> {
        val multipartParts = MultipartBody.Builder()
        multipartParts.addFormDataPart(FIELD_PERMITTED_ID, request.permitterId.toString())
        multipartParts.addFormDataPart(FIELD_DATE_START, request.date)
        return workPermitsApi.createDailyPermit(workPermitId, multipartParts.build().parts)
    }

    companion object {

        private const val FIELD_PERMITTED_ID = "permitter"
        private const val FIELD_DATE_START = "date_start"
    }
}
