package ru.metasharks.catm.feature.profile.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.api.auth.usecase.LogoutUseCase
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.AuthScreen
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.network.offline.CurrentUserIdProvider
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.feature.profile.ui.entities.ProfileUI
import ru.metasharks.catm.feature.profile.ui.entities.mapper.Mapper
import ru.metasharks.catm.feature.profile.usecase.GenerateCommonDocUseCase
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetUserByIdUseCase
import ru.metasharks.catm.utils.requireValue
import ru.metasharks.catm.utils.strings.decodeUtf8
import ru.metasharks.catm.utils.strings.getFileNameFromFileUrl
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val appRouter: ApplicationRouter,
    private val authScreen: AuthScreen,
    private val mediaPreviewScreen: MediaPreviewScreen,
    private val mapper: Mapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val fileManager: FileManager,
    private val generateCommonDocUseCase: GenerateCommonDocUseCase,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    private var userId: Long = -1

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _profileInfo = MutableLiveData<ProfileUI>()
    val profileInfo: LiveData<ProfileUI> = _profileInfo

    private val _isInOfflineLiveData = MutableLiveData<Boolean>()
    val isInOfflineLiveData: LiveData<Boolean> = _isInOfflineLiveData

    private val _codeToProfile = MutableLiveData<Pair<Int, ProfileUI>>()
    val codeToProfile: LiveData<Pair<Int, ProfileUI>> = _codeToProfile

    val isInOffline: Boolean
        get() = isInOfflineLiveData.value ?: false

    var isMyProfile: Boolean = false

    fun loadProfile(userId: Int?, update: Boolean = false) {
        if (userId == null) {
            getCurrentUserUseCase(update)
                .map {
                    this.userId = it.id
                    it.userProfileX
                }
        } else {
            this.userId = userId.toLong()
            getUserByIdUseCase(userId, fromDb = offlineModeProvider.isInOfflineMode)
        }
            .map(mapper::mapProfile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userUI ->
                    _profileInfo.postValue(userUI)
                }, { error ->
                    _error.value = error
                }
            )
    }

    fun back(): Boolean {
        appRouter.exit()
        return true
    }

    fun logout() {
        logoutUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { appRouter.newRootScreen(authScreen()) },
                { error -> _error.value = error }
            )
    }

    fun openFileData(fileUri: String) {
        if (offlineModeProvider.isInOnlineMode) {
            appRouter.navigateTo(mediaPreviewScreen(fileUri, null))
        } else {
            appRouter.navigateTo(
                mediaPreviewScreen(
                    fileManager.getFileUri(
                        filePath = Paths.getProfilePathForId(
                            currentUserIdProvider(),
                            userId,
                        ),
                        fileName = decodeUtf8(getFileNameFromFileUrl(fileUri))
                    ).toString(),
                    null
                )
            )
        }
    }

    fun generateCommonDoc() {
        generateCommonDocUseCase(userId.toInt())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _codeToProfile.postValue(AWAIT_COMMON_DOC to profileInfo.requireValue)
                }, {
                    _error.value = it
                }
            )
    }

    private fun updateProfile(updateCode: Int) {
        if (isMyProfile) {
            getCurrentUserUseCase(initialLoad = true).map { it.userProfileX }
        } else {
            getUserByIdUseCase(userId.toInt(), fromDb = false)
        }
            .map(mapper::mapProfile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _codeToProfile.value = updateCode to it
                }, {
                    _error.value = it
                }
            )
    }

    fun init(userId: Int?) {
        _isInOfflineLiveData.value = offlineModeProvider.isInOfflineMode
        isMyProfile = userId == null
        loadProfile(userId)
    }

    companion object {

        const val TIME_FOR_GENERATE_S = 10L

        const val UPDATE_COMMON_DOC = 1
        const val AWAIT_COMMON_DOC = 2
    }
}
