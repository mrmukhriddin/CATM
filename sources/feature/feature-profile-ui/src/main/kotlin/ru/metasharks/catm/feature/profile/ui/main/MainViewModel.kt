package ru.metasharks.catm.feature.profile.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.AboutAppScreen
import ru.metasharks.catm.core.navigation.screens.CurrentProfileScreen
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.navigation.screens.QrScannerScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitsScreen
import ru.metasharks.catm.core.navigation.screens.WorkersScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.repository.SaveRepository
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.profile.role.RoleProvider
import ru.metasharks.catm.feature.profile.ui.entities.UserUI
import ru.metasharks.catm.feature.profile.ui.entities.mapper.Mapper
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetQrCodeUseCase
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val offlineModeProvider: OfflineModeProvider,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getQrCodeUseCase: GetQrCodeUseCase,
    private val roleProvider: RoleProvider,
    private val appRouter: ApplicationRouter,
    private val fileManager: FileManager,
    private val qrScannerScreen: QrScannerScreen,
    private val profileScreen: CurrentProfileScreen,
    private val workersScreen: WorkersScreen,
    private val mediaPreviewScreen: MediaPreviewScreen,
    private val workPermitsScreen: WorkPermitsScreen,
    private val aboutAppScreen: AboutAppScreen,
    private val mapper: Mapper,
    private val saveRepository: SaveRepository,
) : ViewModel() {

    private val _qrCode = MutableLiveData<ByteArray>()
    val qrCode: LiveData<ByteArray> = _qrCode

    private val _payload = MutableLiveData<Payload>()
    val payload: LiveData<Payload> = _payload

    private val compositeDisposable = CompositeDisposable()

    fun getUser() {
        if (offlineModeProvider.isInOnlineMode) {
            getUserOnline()
        } else {
            getUserOffline()
        }
    }

    @SuppressLint("CheckResult")
    private fun getUserOffline() {
        Singles.zip(
            getCurrentUserUseCase(false)
                .map(mapper::mapUser),
            saveRepository.getSavedSingle(),
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (userUI, offlineSaves) ->
                    listenToSavedChanges()
                    _payload.value = Payload.Offline(
                        user = userUI,
                        role = roleProvider.currentRole,
                        offlineSaves = offlineSaves,
                    )
                }, ::onError
            )
    }

    private fun listenToSavedChanges() {
        saveRepository.getSaved()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _payload.value = Payload.OfflineSavesChanges(it)
                }, {
                    onError(it)
                }
            ).addTo(compositeDisposable)
    }

    private fun onError(error: Throwable, cause: String? = null) {
        _payload.value = Payload.Error(error, cause)
    }

    private fun getUserOnline() {
        getCurrentUserUseCase(false)
            .map(mapper::mapUser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { loadQrCode() }
            .subscribe(
                { userUI ->
                    _payload.value = Payload.Online(
                        user = userUI,
                        role = roleProvider.currentRole,
                    )
                }, ::onError
            )
    }

    private fun loadQrCode() {
        getQrCodeUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { qrCode ->
                    _qrCode.value = qrCode
                }, {
                    onError(it, EXCEPTION_CAUSE_QR)
                }
            )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun openQrScanner() {
        appRouter.navigateTo(qrScannerScreen())
    }

    fun openProfile() {
        appRouter.navigateTo(profileScreen())
    }

    fun openWorkers() {
        appRouter.navigateTo(workersScreen())
    }

    fun openWorkPermits() {
        appRouter.navigateTo(workPermitsScreen())
    }

    fun openAboutAppScreen() {
        appRouter.navigateTo(aboutAppScreen())
    }

    fun startQrCodeActivity(title: String) {
        appRouter.navigateTo(
            mediaPreviewScreen(
                fileManager.getFileUri(QR_CODES_FOLDER, QR_CODE_FILE_NAME).toString(),
                title
            )
        )
    }

    sealed class Payload {

        class Error(
            val error: Throwable,
            val cause: String? = null
        ) : Payload()

        class Offline(
            val user: UserUI,
            val role: Role,
            val offlineSaves: List<OfflineSave>,
        ) : Payload()

        class Online(
            val user: UserUI,
            val role: Role,
        ) : Payload()

        class OfflineSavesChanges(val offlineSaves: List<OfflineSave>) : Payload()
    }

    companion object {

        const val EXCEPTION_CAUSE_QR = "exc_qr"

        private const val QR_CODES_FOLDER = "qr_codes"
        private const val QR_CODE_FILE_NAME = "qr_code.jpg"
    }
}
