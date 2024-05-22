package ru.metasharks.catm.core.storage.file

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileManager @Inject constructor(
    private val context: Context,
) {

    fun getFileFromUri(uri: Uri): File {
        val contentResolver = context.contentResolver
        val fileName = loadFileInformationByUri(uri).first
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    private val providerName: String
        get() = context.applicationContext.packageName + PROVIDER

    fun saveByteArrayAsFile(byteArray: ByteArray, fileName: String, filePath: String?) {
        val dir = filePath?.let { File(context.filesDir, filePath) } ?: context.filesDir
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val fileToWrite = File(dir, fileName)
        try {
            val fos = FileOutputStream(fileToWrite)
            fos.write(byteArray)
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    fun saveFile(file: File, fileName: String, filePath: String?) {
        saveByteArrayAsFile(file.readBytes(), fileName, filePath)
    }

    fun getFileUri(filePath: String, fileName: String): Uri {
        val fileDir = File(context.filesDir, filePath)
        val file = File(fileDir, fileName)
        return FileProvider.getUriForFile(context, providerName, file)
    }

    fun loadFileInformationByUri(uri: Uri): Pair<String, Long> {
        var fileName: String = ""
        var fileSize: Long = 0
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
            fileSize = cursor.getLong(sizeIndex)
        }
        return fileName to fileSize
    }

    fun deleteFiles(path: String?): Boolean {
        val dir = path?.let { File(context.filesDir, path) } ?: context.filesDir
        return dir.deleteRecursively()
    }

    companion object {

        private const val TAG = "FileManager"

        private const val MIME_TYPE_PDF_FILE = "application/pdf"

        private const val PROVIDER = ".provider"
        private const val DOWNLOAD_DIRECTORY = "/x5group"

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val REQUEST_CODE_PERMISSIONS = 1
    }
}
