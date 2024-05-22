package ru.metasharks.catm.utils.strings

import androidx.annotation.StringRes

class StringResWrapper private constructor(
    private val _string: String? = null,
    @StringRes private val _resId: Int? = null,
) {

    val isResource: Boolean = _resId != null

    val resId: Int
        get() = requireNotNull(_resId)

    val string: String
        get() = requireNotNull(_string)

    constructor(_string: String) : this(_string, null)
    constructor(_resId: Int) : this(null, _resId)
}
