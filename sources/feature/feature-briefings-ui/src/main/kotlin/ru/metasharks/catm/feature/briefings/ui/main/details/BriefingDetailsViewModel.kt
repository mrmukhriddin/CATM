package ru.metasharks.catm.feature.briefings.ui.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.BriefingDetailsScreen
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.network.offline.CurrentUserIdProvider
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingDetailsUi
import ru.metasharks.catm.feature.briefings.ui.mappers.BriefingDetailsMapper
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingQuizScreen
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingDetailsUseCase
import ru.metasharks.catm.feature.briefings.usecase.SignBriefingUseCase
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.utils.requireValue
import ru.metasharks.catm.utils.strings.decodeUtf8
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class BriefingDetailsViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val briefingQuizScreen: BriefingQuizScreen,
    private val mediaPreviewScreen: MediaPreviewScreen,
    private val getBriefingDetailsUseCase: GetBriefingDetailsUseCase,
    private val signBriefingUseCase: SignBriefingUseCase,
    private val mapper: BriefingDetailsMapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val fileManager: FileManager,
    private val pendingActionsRepository: PendingActionsRepository,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    private val _payload = MutableLiveData<Payload>()
    val payload: LiveData<Payload> = _payload

    private val _briefing = MutableLiveData<BriefingDetailsUi>()
    val briefing: LiveData<BriefingDetailsUi> = _briefing

    private val _signLoading = MutableLiveData(false)
    val signLoading: LiveData<Boolean> = _signLoading

    private val _isInOfflineLiveData = MutableLiveData<Boolean>()
    val isInOfflineLiveData: LiveData<Boolean> = _isInOfflineLiveData

    val isInOffline: Boolean
        get() = isInOfflineLiveData.value ?: false

    private var briefingId: Int = 0

    fun init() {
        _isInOfflineLiveData.value = offlineModeProvider.isInOfflineMode
        loadData()
    }

    fun initialData(briefingId: Int) {
        this.briefingId = briefingId
    }

    fun openBriefingQuiz() {
        appRouter.navigateToWithResult<Boolean>(briefingQuizScreen(briefingId)) { passed ->
            if (passed) {
                if (isInOffline.not()) {
                    getBriefingDetailsUseCase(fromCache = true, id = briefingId)
                        .map { briefing -> mapper.map(briefing, quizPassed = true) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { briefing ->
                                refreshBriefing()
                                _briefing.value = briefing
                            }, { error ->
                                Timber.e(error)
                            }
                        )
                } else {
                    checkPending()
                }
            }
        }
    }

    fun refreshBriefing() {
        getBriefingDetailsUseCase(briefingId, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun openFile(fileUri: String) {
        val fileLink = if (isInOffline) {
            fileManager.getFileUri(
                filePath = Paths.getBriefingPathForId(
                    userId = currentUserIdProvider(),
                    briefingId = briefingId.toLong()
                ),
                fileName = decodeUtf8(getFileNameFromFileUrl(fileUrl = fileUri))
            ).toString()
        } else {
            fileUri
        }
        appRouter.navigateTo(mediaPreviewScreen(fileLink, null))
    }

    fun exit() {
        appRouter.exit()
    }

    fun loadData() {
        getBriefingDetailsUseCase(fromCache = true, id = briefingId)
            .map(mapper::map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { briefing ->
                    this._briefing.value = briefing
                    if (isInOffline) {
                        checkPending()
                    }
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    private fun checkPending() {
        Singles.zip(
            pendingActionsRepository.getPayloads(PendingActionPayload.SignBriefing::class),
            pendingActionsRepository.getPayloads(PendingActionPayload.PassQuiz::class)
        )
            .flatMapMaybe { (pendingSign, pendingQuiz) ->
                val currentPendingSign = pendingSign.find { it.briefingId == briefingId }
                val currentPendingQuiz =
                    pendingQuiz.find { it.passQuizRequest.quizId == briefing.requireValue.quizId }
                if (currentPendingSign == null && currentPendingQuiz == null) {
                    Maybe.empty()
                } else {
                    Maybe.just(currentPendingSign to currentPendingQuiz)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (pendingActionSign, pendingActionQuiz) ->
                    if (pendingActionSign != null) {
                        _payload.value = Payload.OutgoingSigning
                    }
                    if (pendingActionQuiz != null) {
                        _payload.value = Payload.OutgoingQuiz
                    }
                },
                {
                    Timber.e(it)
                },
            )
    }

    fun signBriefing() {
        if (offlineModeProvider.isInOnlineMode) {
            signBriefingOnline()
        } else {
            signBriefingOffline()
        }
    }

    private fun signBriefingOffline() {
        pendingActionsRepository.savePendingAction(PendingActionPayload.SignBriefing(briefingId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.OutgoingSigning
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun signBriefingOnline() {
        _signLoading.value = true
        signBriefingUseCase(briefingId)
            .andThen(
                getBriefingDetailsUseCase(fromCache = true, id = briefingId)
                    .map { briefing -> mapper.map(briefing, signed = true) }
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { briefing ->
                    appRouter.sendResultBy(BriefingDetailsScreen.KEY, briefingId)
                    refreshBriefing()
                    _signLoading.value = false
                    _briefing.value = briefing
                }, { error ->
                    _signLoading.value = false
                    Timber.e(error)
                }
            )
    }

    sealed class Payload {

        object OutgoingSigning : Payload()

        object OutgoingQuiz : Payload()
    }
}
