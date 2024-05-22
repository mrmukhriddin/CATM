package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.db.BriefingTypeDao
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface GetBriefingTypesUseCase {

    operator fun invoke(): Single<List<BriefingTypeX>>
}

internal class GetBriefingTypesUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
    private val dao: BriefingTypeDao,
) : GetBriefingTypesUseCase {

    override fun invoke(): Single<List<BriefingTypeX>> = api.getTypes()
        .flatMap { types ->
            dao.insertAllTypes(types)
                .andThen(Single.just(types))
        }
}
