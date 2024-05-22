package ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.createworkpermit.ui.R
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttachmentViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
    private val fileManager: FileManager,
) : ViewModel() {

    private val errorFileToBigException =
        applicationContext.getString(R.string.error_text_file_to_big)

    private val _fileInfo = MutableLiveData<AttachmentInfo?>(null)
    val fileInfo: LiveData<AttachmentInfo?> = _fileInfo

    private val _warning = MutableLiveData<String>()
    val warning: LiveData<String> = _warning

    fun loadFileByUri(uri: Uri) {
        Single.fromCallable { fileManager.loadFileInformationByUri(uri) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (name, size) ->
                    if (size <= MAX_FILE_SIZE_BYTES) {
                        _fileInfo.value = AttachmentInfo(uri, name, size)
                    } else {
                        _warning.value = errorFileToBigException
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    fun clearFileInfo() {
        _fileInfo.value = null
    }

    fun restore(it: AttachmentFragment.AttachmentRestoreData) {
        _fileInfo.value = it.attachmentInfo
    }

    @Parcelize
    class AttachmentInfo(
        val uri: Uri,
        val name: String,
        val size: Long
    ) : Parcelable

    companion object {

        private const val MAX_FILE_SIZE_BYTES = 15 * 1024 * 1024
    }
}
