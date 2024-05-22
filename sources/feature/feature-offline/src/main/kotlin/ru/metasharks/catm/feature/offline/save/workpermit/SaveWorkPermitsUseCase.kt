package ru.metasharks.catm.feature.offline.save.workpermit

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.kotlin.zipWith
import ru.metasharks.catm.feature.offline.db.WorkPermitCreateOfflineToolsDao
import ru.metasharks.catm.feature.offline.save.DownloadFileUseCase
import ru.metasharks.catm.feature.offline.save.Paths.getWorkPermitPathForId
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetOrganizationUseCase
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitMiniDb
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitX
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX
import ru.metasharks.catm.feature.workpermit.usecase.GetEmployeesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetOptionsUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetResponsibleEmployeesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetStatusesUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitsUseCase
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import javax.inject.Inject

fun interface SaveWorkPermitsUseCase {

    operator fun invoke(): Completable
}

internal class SaveWorkPermitsUseCaseImpl @Inject constructor(
    private val workPermitDao: WorkPermitDao,
    private val getWorkPermitsUseCase: GetWorkPermitsUseCase,
    private val getWorkPermitDetailsUseCase: GetWorkPermitUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStatusesUseCase: GetStatusesUseCase,

    private val getOrganizationUseCase: GetOrganizationUseCase,
    private val getResponsibleEmployeesUseCase: GetResponsibleEmployeesUseCase,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getOptionsUseCase: GetOptionsUseCase,
    private val workPermitCreateOfflineToolsDao: WorkPermitCreateOfflineToolsDao
) : SaveWorkPermitsUseCase {

    override fun invoke(): Completable {
        return Observable.create<List<WorkPermitX>> { emitter ->
            var next: String? = null
            do {
                val envelope = getWorkPermitsUseCase(StatusX.ALL, next, null).blockingGet()
                emitter.onNext(envelope.results)
                next = envelope.next
            } while (next != null)
            emitter.onComplete()
        }
            .reduce(emptyList<WorkPermitX>()) { a, next ->
                a + next.filter { it.status != StatusCode.ARCHIVED.code }
            }
            .flatMap {
                getCurrentUserUseCase(initialLoad = false)
                    .flatMap { user ->
                        workPermitDao.insert(it.map { workPermit ->
                            WorkPermitMiniDb(
                                id = workPermit.id,
                                userId = user.id,
                                data = workPermit
                            )
                        }).toSingle { it }
                    }
            }
            .flattenAsObservable(Functions.identity())
            .flatMapCompletable {
                saveWorkPermit(it)
            }.andThen(getCurrentUserUseCase(initialLoad = false))
            .flatMap {
                saveTools(it)
            }.flatMapCompletable {
                getStatusesUseCase(fromDb = false).ignoreElement()
            }
    }

    /**
     * Список вещей для сохранения:
     * - главный документ
     * - дополнительные документы
     */
    private fun saveWorkPermit(workPermitX: WorkPermitX): Completable {
        if (workPermitX.status == StatusCode.ARCHIVED.code) {
            return Completable.complete()
        }
        return Singles.zip(
            getCurrentUserUseCase(initialLoad = false),
            getWorkPermitDetailsUseCase(workPermitX.id, forceRefresh = true)
        )
            .flatMap { (user, workPermitDetails) ->
                downloadMainDocument(workPermitDetails, user.id)
                    .flatMap { downloadAdditionalDocuments(workPermitDetails, user.id) }
            }.flatMapCompletable { Completable.complete() }
    }

    private fun downloadMainDocument(
        workPermitDetails: WorkPermitDetailsX,
        currentUserId: Long
    ): Single<Unit> {
        val fileUri = workPermitDetails.mainInfo.document?.fileUrl ?: return Single.just(Unit)
        return downloadFile(currentUserId, workPermitDetails.mainInfo.id, fileUri)
    }

    private fun downloadAdditionalDocuments(
        workPermitDetails: WorkPermitDetailsX,
        userId: Long
    ): Single<Unit> {
        val additionalDocuments = workPermitDetails.documents
        if (additionalDocuments.isEmpty()) {
            return Single.just(Unit)
        }
        return Observable.fromIterable(additionalDocuments)
            .flatMapSingle { downloadFile(userId, workPermitDetails.mainInfo.id, it.fileUrl) }
            .toList(additionalDocuments.size)
            .map { }
    }

    private fun saveTools(user: UserDb): Single<Unit> {
        return Singles.zip(
            getOrganizationUseCase(),
            getOptionsUseCase(),
        ).zipWith(
            Singles.zip(
                getAllResponsible(),
                getAllExceptResponsible(),
            )
        ).map { (organizationAndOptions, responsibleAndNon) ->
            val (organization, options) = organizationAndOptions
            val (responsible, exceptResponsible) = responsibleAndNon
            WorkPermitCreateOfflineTools(
                userId = user.id,
                organization = organization,
                responsibleEmployees = responsible,
                exceptResponsibleEmployees = exceptResponsible,
                allOptions = options
            )
        }.flatMapCompletable { workPermitCreateOfflineToolsDao.insert(it) }.toSingle { }
    }

    private fun getAllResponsible(): Single<List<WorkPermitUserX>> {
        return Observable.create<List<WorkPermitUserX>> { emitter ->
            var next: String? = null
            do {
                val envelope =
                    getResponsibleEmployeesUseCase(nextUrl = next, query = null).blockingGet()
                emitter.onNext(envelope.results)
                next = envelope.next
            } while (next != null)
            emitter.onComplete()
        }.reduce(emptyList()) { a, next ->
            a + next
        }
    }

    private fun getAllExceptResponsible(): Single<List<WorkPermitUserX>> {
        return Observable.create<List<WorkPermitUserX>> { emitter ->
            var next: String? = null
            do {
                val envelope =
                    getEmployeesUseCase(nextUrl = next, query = null).blockingGet()
                emitter.onNext(envelope.results)
                next = envelope.next
            } while (next != null)
            emitter.onComplete()
        }.reduce(emptyList()) { a, next ->
            a + next
        }
    }

    private fun downloadFile(
        currentUserId: Long,
        workPermitId: Long,
        fileUri: String
    ): Single<Unit> {
        val path = getWorkPermitPathForId(userId = currentUserId, workPermitId = workPermitId)
        val fileName = getFileNameFromFileUrl(fileUri)
        return downloadFileUseCase(
            fileUri = fileUri,
            fileName = fileName,
            path = path
        ).toSingle {}
    }
}
