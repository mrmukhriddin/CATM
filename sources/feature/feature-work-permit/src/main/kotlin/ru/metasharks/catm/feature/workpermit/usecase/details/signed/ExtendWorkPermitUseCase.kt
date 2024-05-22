package ru.metasharks.catm.feature.workpermit.usecase.details.signed

import io.reactivex.rxjava3.core.Completable
import okhttp3.MultipartBody
import ru.metasharks.catm.feature.workpermit.entities.certain.extend.ExtendWorkPermitDataX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface ExtendWorkPermitUseCase {

    operator fun invoke(workPermitId: Long, data: ExtendWorkPermitDataX): Completable
}

internal class ExtendWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : ExtendWorkPermitUseCase {

    override fun invoke(workPermitId: Long, data: ExtendWorkPermitDataX): Completable {
        val multipartParts = MultipartBody.Builder()
        multipartParts.addFormDataPart(FIELD_PERMIT_ISSUER_ID, data.permitIssuerId.toString())
        multipartParts.addFormDataPart(FIELD_DATE_END, data.dateEnd)
        return workPermitsApi.extendWorkPermit(workPermitId, multipartParts.build().parts)
    }

    companion object {

        private const val FIELD_PERMIT_ISSUER_ID = "permit_issuer"
        private const val FIELD_DATE_END = "date_end"
    }
}
