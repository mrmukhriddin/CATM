package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.db.BriefingsDao
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface GetBriefingDetailsUseCase {

    operator fun invoke(id: Int, fromCache: Boolean): Single<BriefingX>
}

internal class GetBriefingDetailsUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
    private val dao: BriefingsDao,
) : GetBriefingDetailsUseCase {

    override fun invoke(id: Int, fromCache: Boolean): Single<BriefingX> {
        val fromApi = api.getBriefings()
            .flatMap { briefings ->
                dao.insertAllBriefings(briefings)
                    .andThen(Single.just(briefings))
            }
            .map { items -> items.first { it.id == id } }

        return if (fromCache) {
            dao.getBriefingById(id)
                .switchIfEmpty(fromApi)
        } else {
            fromApi
        }
    }
}
