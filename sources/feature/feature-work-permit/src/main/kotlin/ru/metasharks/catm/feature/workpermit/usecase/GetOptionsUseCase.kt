package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.options.OptionsEnvelopeX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetOptionsUseCase {

    operator fun invoke(): Single<OptionsEnvelopeX>
}

class GetOptionsUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : GetOptionsUseCase {

    override fun invoke(): Single<OptionsEnvelopeX> {
        return workPermitsApi.getOptions()
    }
}
