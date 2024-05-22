package ru.metasharks.catm.feature.offline.save.profile

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import ru.metasharks.catm.feature.offline.save.DownloadFileUseCase
import ru.metasharks.catm.feature.offline.save.Paths.getProfilePathForId
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetUserByIdUseCase
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import javax.inject.Inject

fun interface SaveProfileUseCase {

    operator fun invoke(userId: Long?): Completable
}

internal class SaveProfileUseCaseImpl @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
) : SaveProfileUseCase {

    /**
     * Список вещей для сохранения:
     * - медосмотро
     * - все тренинги
     * - карточка
     * - документ
     */
    override fun invoke(userId: Long?): Completable {
        return if (userId == null) {
            downloadCurrentUser()
        } else {
            downloadUser(userId)
        }
    }

    private fun downloadCurrentUser(): Completable {
        return getCurrentUserUseCase(initialLoad = true)
            .map { it.userProfileX }
            .flatMap { user ->
                downloadMedicalExam(user, user.id)
                    .flatMap { downloadEquipmentCard(user, user.id) }
                    .flatMap { downloadTrainings(user, user.id) }
                    .flatMap { downloadCommonDocument(user, user.id) }
            }.ignoreElement()
    }

    private fun downloadUser(userId: Long): Completable {
        return Singles.zip(
            getCurrentUserUseCase(initialLoad = false),
            getUserByIdUseCase(userId.toInt(), fromDb = false)
        ).flatMap { (currentUser, downloadingUser) ->
            downloadMedicalExam(downloadingUser, currentUser.id)
                .flatMap { downloadEquipmentCard(downloadingUser, currentUser.id) }
                .flatMap { downloadTrainings(downloadingUser, currentUser.id) }
                .flatMap { downloadCommonDocument(downloadingUser, currentUser.id) }
        }.ignoreElement()
    }

    private fun downloadMedicalExam(user: UserProfileX, currentUserId: Long): Single<Unit> {
        val fileUri = user.medicalExam?.fileUri ?: return Single.just(Unit)
        return downloadFile(currentUserId, user.id, fileUri)
    }

    private fun downloadEquipmentCard(user: UserProfileX, currentUserId: Long): Single<Unit> {
        val fileUri = user.protectiveEquipmentCard?.fileUri ?: return Single.just(Unit)
        return downloadFile(currentUserId, user.id, fileUri)
    }

    private fun downloadCommonDocument(user: UserProfileX, currentUserId: Long): Single<Unit> {
        val fileUri = user.commonDoc?.document ?: return Single.just(Unit)
        return downloadFile(currentUserId, user.id, fileUri)
    }

    private fun downloadTrainings(user: UserProfileX, currentUserId: Long): Single<Unit> {
        val trainings = user.trainings
        if (trainings.isNullOrEmpty()) {
            return Single.just(Unit)
        }
        return Observable.fromIterable(trainings)
            .flatMapSingle { downloadFile(currentUserId, user.id, it.fileUri) }
            .toList(trainings.size)
            .map { }
    }

    private fun downloadFile(currentUserId: Long, userId: Long, fileUri: String): Single<Unit> {
        val path = getProfilePathForId(userId = currentUserId, profileId = userId)
        val fileName = getFileNameFromFileUrl(fileUri)
        return downloadFileUseCase(
            fileUri = fileUri,
            fileName = fileName,
            path = path
        ).toSingle {}
    }
}
