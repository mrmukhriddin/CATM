package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.db.BriefingsDao
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface GetBriefingsUseCase {

    operator fun invoke(fromCache: Boolean): Single<List<BriefingX>>
}

internal class GetBriefingsUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
    private val dao: BriefingsDao,
) : GetBriefingsUseCase {

    override fun invoke(fromCache: Boolean): Single<List<BriefingX>> {
        return if (fromCache) {
            dao.getBriefings()
        } else {
            api.getBriefings()
                .flatMap { briefings ->
                    dao.insertAllBriefings(briefings)
                        .andThen(Single.just(briefings))
                }
        }
    }
}
