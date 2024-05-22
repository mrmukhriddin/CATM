package ru.metasharks.catm.feature.offline.save.briefings

import io.reactivex.rxjava3.core.Maybe
import ru.metasharks.catm.feature.offline.db.BriefingOfflineDao
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import javax.inject.Inject

fun interface GetBriefingOfflineDataUseCase {

    operator fun invoke(): Maybe<BriefingOfflineData>
}

internal class GetBriefingOfflineDataUseCaseImpl @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val dao: BriefingOfflineDao,
) : GetBriefingOfflineDataUseCase {

    override fun invoke(): Maybe<BriefingOfflineData> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapMaybe { dao.getBriefingsForUser(it.id) }
    }
}
