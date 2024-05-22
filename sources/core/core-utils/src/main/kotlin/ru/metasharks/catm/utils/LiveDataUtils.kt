package ru.metasharks.catm.utils

import androidx.lifecycle.LiveData

val <T> LiveData<T>.requireValue: T
    get() = requireNotNull(value)
