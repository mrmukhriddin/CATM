package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface GetQrCodeUseCase {

    operator fun invoke(): Single<ByteArray>
}

internal class GetQrCodeUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val fileManager: FileManager,
) : GetQrCodeUseCase {

    override fun invoke(): Single<ByteArray> {
        return profileApi.getQr()
            .map {
                val bytes = it.bytes()
                fileManager.saveByteArrayAsFile(bytes, "qr_code.jpg", "qr_codes")
                bytes
            }
    }
}
