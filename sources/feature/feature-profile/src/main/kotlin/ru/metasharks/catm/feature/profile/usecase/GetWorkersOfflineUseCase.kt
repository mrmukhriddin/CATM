package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.db.UserDao
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import javax.inject.Inject

fun interface GetWorkersOfflineUseCase {

    operator fun invoke(): Single<List<UserProfileX>>
}

internal class GetWorkersOfflineUseCaseImpl @Inject constructor(
    private val userDao: UserDao,
) : GetWorkersOfflineUseCase {

    override operator fun invoke(): Single<List<UserProfileX>> {
        return userDao.getUserProfiles()
    }
}
