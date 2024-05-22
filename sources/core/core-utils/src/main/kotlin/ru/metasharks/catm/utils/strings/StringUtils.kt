package ru.metasharks.catm.utils.strings

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

val String.Companion.empty: String
    get() = ""

fun getFileTypeAndSizeString(fileUri: String, sizeInBytes: Int): String {
    val extension = getExtension(fileUri)
    val sizeString = getSizeString(sizeInBytes)
    return "$extension $sizeString"
}

fun getExtension(fileUri: String): String {
    return fileUri.substringAfterLast('.')
}

fun getSizeString(sizeInBytes: Int): String {
    return getSizeString(sizeInBytes.toLong())
}

fun getSizeString(sizeInBytes: Long): String {
    check(sizeInBytes > 0) { "File size can not be 0 or less" }
    var size: Float = sizeInBytes.toFloat()
    var grade = 0
    while (size >= BYTES_IN_KILO) {
        size /= BYTES_IN_KILO
        grade++
    }
    val measure = when (grade) {
        GRADE_BYTE -> "b"
        GRADE_KILOBYTE -> "kb"
        GRADE_MEGABYTE -> "mb"
        GRADE_GIGABYTE -> "gb"
        else -> throw IllegalArgumentException("Too big size")
    }
    val sizeString = String.format("%.2f", size)
    return "$sizeString $measure"
}

fun getFileNameFromFileUrl(fileUrl: String): String {
    return fileUrl.substringAfterLast('/')
}

fun decodeUtf8(encoded: String): String {
    return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
}

private const val BYTES_IN_KILO = 1024F

private const val GRADE_BYTE = 0
private const val GRADE_KILOBYTE = 1
private const val GRADE_MEGABYTE = 2
private const val GRADE_GIGABYTE = 3
