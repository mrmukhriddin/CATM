package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.network.offline.CurrentUserIdProvider
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.profile.db.UserDao
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.role.RoleManager
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface GetCurrentUserUseCase {

    operator fun invoke(initialLoad: Boolean): Single<UserDb>
}

internal class GetCurrentUserUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val userDao: UserDao,
    private val roleManager: RoleManager,
    private val offlineModeProvider: OfflineModeProvider,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : GetCurrentUserUseCase {

    override fun invoke(initialLoad: Boolean): Single<UserDb> {
        return if (initialLoad) {
            getFromServer()
        } else {
            getFromDb().switchIfEmpty(
                Single.fromCallable {
                    if (offlineModeProvider.isInOfflineMode) {
                        offlineModeProvider.isInOfflineMode = false
                    }
                }.flatMap { getFromServer() }
            )
        }
    }

    private fun getFromDb(): Maybe<UserDb> {
        return userDao.getUser()
    }

    private fun getFromServer(): Single<UserDb> {
        return profileApi.getUser().flatMap { user ->
            Single.fromCallable {
                roleManager.setRole(user.permission)
                userDao.deleteAll()
            }.flatMap {
                currentUserIdProvider.currentUserId = user.id
                val userDb = UserDb(
                    id = user.id,
                    userProfileX = user
                )
                userDao.insert(userDb)
                    .map { userDb }
            }
        }
    }
}
