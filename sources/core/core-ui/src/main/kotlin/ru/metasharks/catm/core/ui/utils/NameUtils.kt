package ru.metasharks.catm.core.ui.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.toSpannable
import ru.metasharks.catm.core.ui.R
import ru.tinkoff.decoro.Mask
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots

fun getShortFormOfFullName(first: String, second: String, third: String?): String {
    val res = "$first ${second.first()}."
    return third?.let { res + " ${it.first()}." } ?: res
}

fun getFullNameWithLineBreak(first: String, second: String, third: String? = null): String {
    return requireNotNull(joinStrings('\n', first, second, third))
}

fun getFullName(first: String, second: String, third: String? = null): String {
    return requireNotNull(joinStrings(' ', first, second, third))
}

fun joinStrings(delimiter: Char, vararg stringsToAppend: String?): String? {
    val stringBuilder = StringBuilder()
    var i = stringsToAppend.indexOfFirst { it != null }
    if (i == -1) return null
    stringBuilder.append(stringsToAppend[i++])
    while (i < stringsToAppend.size) {
        val string = stringsToAppend[i]
        if (string != null) {
            stringBuilder.append(delimiter).append(string)
        }
        i++
    }
    return stringBuilder.toString()
}

fun getFirstLetters(lettersAmount: Int, vararg stringsToAppend: String?): String? {
    var i = stringsToAppend.indexOfFirst { it != null }
    if (i == -1) return null

    var charIndex = 0
    val charArray = CharArray(lettersAmount)

    while (i < stringsToAppend.size && charIndex < lettersAmount) {
        val string = stringsToAppend[i]
        if (string != null) {
            charArray[charIndex++] = string.first().uppercaseChar()
        }
        i++
    }
    return charArray.concatToString()
}

fun getFirstWords(vararg strings: String?): List<String> {
    var i = strings.indexOfFirst { it != null }
    if (i == -1) return emptyList()

    val words = mutableListOf<String>()
    words.add(requireNotNull(strings[i++]))

    while (i < strings.size) {
        val word = strings[i++]
        if (word != null) {
            words.add(word.substringBefore(' '))
        }
    }
    return words
}

fun String?.getFirstLetters(lettersAmount: Int): String? {
    if (this == null) return null
    var i = 0
    var index = 0
    val charArray = CharArray(lettersAmount)

    while (i < lettersAmount && index != -1 && index < length) {
        charArray[i++] = this[index].uppercaseChar()
        index = this.indexOf(' ', index) + 1
        if (index == 0) {
            break
        }
    }

    return charArray.concatToString()
}

fun formatNumber(phoneNumber: String?): String? {
    if (phoneNumber == null) return null

    val mask: Mask = MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true)
    mask.insertFront(phoneNumber)
    return mask.toString()
}

fun getHighlightedString(
    context: Context,
    @StringRes textLabelId: Int,
    secondPart: String,
): Spannable {
    val color = ContextCompat.getColor(context, R.color.dark_gray)
    val ssb = SpannableStringBuilder()
        .append(context.getString(textLabelId)).append(' ')
        .color(color) { append(secondPart) }
    return ssb.toSpannable()
}
