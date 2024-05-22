package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.db.UserDao
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

interface GetUserByIdUseCase {

    operator fun invoke(userId: Int, fromDb: Boolean): Single<UserProfileX>
}

internal class GetUserByIdUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val userDao: UserDao,
) : GetUserByIdUseCase {

    private fun getFromServer(userId: Int, fromDb: Boolean): Single<UserProfileX> {
        return if (fromDb) {
            getFromDb(userId)
                .switchIfEmpty(
                    profileApi.getUser(userId).flatMap {
                        userDao.insert(it).toSingle { it }
                    }
                )
        } else {
            profileApi.getUser(userId).flatMap {
                userDao.insert(it).toSingle { it }
            }
        }
    }

    override fun invoke(userId: Int, fromDb: Boolean): Single<UserProfileX> {
        return getFromServer(userId, fromDb)
    }

    private fun getFromDb(userId: Int): Maybe<UserProfileX> {
        return userDao.getUserProfile(userId)
    }
}
