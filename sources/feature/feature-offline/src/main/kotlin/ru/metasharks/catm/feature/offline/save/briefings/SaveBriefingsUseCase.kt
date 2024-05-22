package ru.metasharks.catm.feature.offline.save.briefings

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.kotlin.Singles
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingCategoriesUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingTypesUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingsUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetQuizUseCase
import ru.metasharks.catm.feature.offline.db.BriefingOfflineDao
import ru.metasharks.catm.feature.offline.save.DownloadFileUseCase
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.utils.strings.decodeUtf8
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import javax.inject.Inject

fun interface SaveBriefingsUseCase {

    operator fun invoke(): Completable
}

internal class SaveBriefingsUseCaseImpl @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBriefingsUseCase: GetBriefingsUseCase,
    private val getBriefingCategoriesUseCase: GetBriefingCategoriesUseCase,
    private val getBriefingTypesUseCase: GetBriefingTypesUseCase,
    private val getQuizUseCase: GetQuizUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val briefingOfflineDao: BriefingOfflineDao,
) : SaveBriefingsUseCase {

    private fun getBriefingsWithQuiz(): Single<List<BriefingQuizX>> {
        return getBriefingsUseCase(fromCache = false)
            .flattenAsObservable(Functions.identity())
            .flatMapSingle { briefing ->
                enrichWithQuiz(briefing)
            }.toList()
    }

    private fun enrichWithQuiz(briefing: BriefingX): Single<BriefingQuizX> {
        return if (briefing.quiz == null) {
            Single.just(
                BriefingQuizX(
                    briefingX = briefing,
                    quizX = null
                )
            )
        } else {
            getQuizUseCase(briefing.quiz!!).map {
                BriefingQuizX(
                    briefingX = briefing,
                    quizX = it,
                )
            }
        }
    }

    override fun invoke(): Completable {
        return Singles.zip(
            getBriefingCategoriesUseCase(),
            getBriefingTypesUseCase(),
            getBriefingsWithQuiz(),
        ).flatMap { (categories, types, briefings) ->
            getCurrentUserUseCase(initialLoad = false)
                .map { user ->
                    BriefingOfflineData(
                        userId = user.id,
                        categories = categories,
                        types = types,
                        briefings = briefings,
                    )
                }
        }.flatMapObservable {
            briefingOfflineDao.insert(it).andThen(
                Observable.fromIterable(it.briefings)
            )
        }.flatMapCompletable { briefingQuiz ->
            getCurrentUserUseCase(initialLoad = false).flatMapCompletable {
                saveBriefing(briefingQuiz, it.id)
            }
        }
    }

    /**
     * Список вещей для сохранения:
     * - тесты
     * - файлы
     */
    private fun saveBriefing(briefing: BriefingQuizX, currentUserId: Long): Completable {
        return Single.just(briefing)
            .flatMap {
                downloadFile(it.briefingX, currentUserId)
            }.ignoreElement()
    }

    private fun downloadFile(briefing: BriefingX, currentUserId: Long): Single<Unit> {
        val fileUrl = briefing.file ?: return Single.just(Unit)
        val fileName = decodeUtf8(getFileNameFromFileUrl(fileUrl))
        val path =
            Paths.getBriefingPathForId(briefingId = briefing.id.toLong(), userId = currentUserId)
        return downloadFileUseCase(fileUri = fileUrl, fileName = fileName, path = path).toSingle { }
    }
}
