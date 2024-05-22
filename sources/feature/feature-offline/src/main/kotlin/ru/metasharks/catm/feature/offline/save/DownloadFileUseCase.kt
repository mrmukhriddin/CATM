package ru.metasharks.catm.feature.offline.save

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.offline.service.OfflineApi
import ru.metasharks.catm.utils.strings.decodeUtf8
import javax.inject.Inject

fun interface DownloadFileUseCase {

    operator fun invoke(fileUri: String, fileName: String, path: String?): Completable
}

internal class DownloadFileUseCaseImpl @Inject constructor(
    private val offlineApi: OfflineApi,
    private val fileManager: FileManager,
) : DownloadFileUseCase {

    override fun invoke(fileUri: String, fileName: String, path: String?): Completable {
        return offlineApi.downloadFile(fileUri)
            .flatMap { body ->
                Single.fromCallable {
                    fileManager.saveByteArrayAsFile(
                        byteArray = body.bytes(),
                        fileName = decodeUtf8(fileName),
                        filePath = path
                    )
                }
            }.flatMapCompletable { Completable.complete() }
    }
}
