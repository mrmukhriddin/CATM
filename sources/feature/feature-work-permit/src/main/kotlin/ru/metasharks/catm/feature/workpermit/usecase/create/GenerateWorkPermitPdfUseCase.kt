package ru.metasharks.catm.feature.workpermit.usecase.create

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GenerateWorkPermitPdfUseCase {

    operator fun invoke(workPermitId: Long): Completable
}

internal class GenerateWorkPermitPdfUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : GenerateWorkPermitPdfUseCase {

    override fun invoke(workPermitId: Long): Completable {
        return workPermitsApi.generateWorkPermitPdf(workPermitId)
    }
}
