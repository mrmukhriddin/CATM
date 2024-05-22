package ru.metasharks.catm.feature.workpermit.ui.details.adddocs

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsAdditionalDocumentsScreen
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitFromDbViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkPermitsMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.DeleteAdditionalDocumentUseCase
import ru.metasharks.catm.feature.workpermit.usecase.details.UploadAdditionalDocumentUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class AdditionalDocumentsViewModel @Inject constructor(
    getWorkPermitUseCase: GetWorkPermitUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    mapper: WorkPermitsMapper,
    @ApplicationContext applicationContext: Context,
    private val uploadAdditionalDocumentUseCase: UploadAdditionalDocumentUseCase,
    private val deleteAdditionalDocumentUseCase: DeleteAdditionalDocumentUseCase,
    private val fileManager: FileManager,
    private val mediaPreviewScreen: MediaPreviewScreen,
    override val appRouter: ApplicationRouter,
) : WorkPermitFromDbViewModel(getWorkPermitUseCase, getCurrentUserUseCase, mapper, appRouter) {

    private val errorFileToBigException =
        applicationContext.getString(R.string.error_text_file_to_big)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _fileInfo = MutableLiveData<AttachmentInfo?>(null)
    val fileInfo: LiveData<AttachmentInfo?> = _fileInfo

    private val _fileLoaded = MutableLiveData<Pair<DocumentUi, Boolean>>()
    val fileLoaded: LiveData<Pair<DocumentUi, Boolean>> = _fileLoaded

    private val _warning = MutableLiveData<String>()
    val warning: LiveData<String> = _warning

    private val loading: Boolean
        get() = isLoading.value ?: false

    private var updateCountOfFiles = 0

    fun loadFileByUri(uri: Uri) {
        if (loading) {
            return
        }
        Single.fromCallable { fileManager.loadFileInformationByUri(uri) }
            .map { (name, size) -> AttachmentInfo(uri, name, size) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { attachmentInfo ->
                    if (attachmentInfo.size <= MAX_FILE_SIZE_BYTES) {
                        _fileInfo.value =
                            AttachmentInfo(uri, attachmentInfo.name, attachmentInfo.size)
                    } else {
                        _warning.value = errorFileToBigException
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    fun openDocument(fileUrl: String) {
        appRouter.navigateTo(mediaPreviewScreen(fileUrl, null))
    }

    fun uploadFileToServer(attachmentInfo: AttachmentInfo) {
        _isLoading.value = true
        Single.fromCallable { fileManager.getFileFromUri(attachmentInfo.uri) }
            .flatMap { uploadAdditionalDocumentUseCase(workPermitId, it) }
            .map { mapper.mapDocument(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _isLoading.value = false }
            .subscribe(
                { document ->
                    updateCountOfFiles++
                    _fileLoaded.value = document to true
                }, {
                    Timber.e(it)
                }
            )
    }

    fun deleteFileFromServer(file: DocumentUi) {
        if (loading) {
            return
        }
        _isLoading.value = true
        deleteAdditionalDocumentUseCase(workPermitId, file.fileId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                _isLoading.value = false
            }
            .subscribe(
                {
                    updateCountOfFiles--
                    _fileLoaded.value = file to false
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    override fun exit() {
        if (updateCountOfFiles != 0) {
            appRouter.sendResultBy(
                WorkPermitDetailsAdditionalDocumentsScreen.KEY,
                (workPermit.value?.additionalDocumentsCount ?: 0) + updateCountOfFiles
            )
        }
        super.exit()
    }

    companion object {

        private const val MAX_FILE_SIZE_BYTES = 15 * 1024 * 1024
    }

    @Parcelize
    class AttachmentInfo(
        val uri: Uri,
        val name: String,
        val size: Long
    ) : Parcelable
}
