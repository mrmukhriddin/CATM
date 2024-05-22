package ru.metasharks.catm.utils

import kotlin.reflect.KProperty

interface LazyProvider<A, T> {
    operator fun provideDelegate(thisRef: A, prop: KProperty<*>): Lazy<T>
}
