package ru.metasharks.catm.core.network

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FileConverter {

    private val mimeTypeMap: MimeTypeMap
        get() = MimeTypeMap.getSingleton()

    fun fileToMultipart(file: File, name: String): MultipartBody.Part {
        val fileExtension = file.extension

        val mimeType =
            requireNotNull(mimeTypeMap.getMimeTypeFromExtension(fileExtension))
        val filePart = MultipartBody.Part.createFormData(
            name,
            file.name,
            file.asRequestBody(mimeType.toMediaType())
        )
        return filePart
    }
}
