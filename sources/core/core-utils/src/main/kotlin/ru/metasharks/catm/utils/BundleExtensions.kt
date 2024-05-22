package ru.metasharks.catm.utils

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

inline fun <F, reified T> argumentDelegate(
    crossinline provideArguments: (F) -> Bundle?,
    argKey: String? = null
): LazyProvider<F, T> =
    object : LazyProvider<F, T> {
        override fun provideDelegate(thisRef: F, prop: KProperty<*>) =
            lazy {
                val bundle = provideArguments(thisRef)
                if (argKey == null) {
                    bundle?.get(prop.name) as T
                } else {
                    bundle?.get(argKey) as T
                }
            }
    }

inline fun <reified T> Activity.argumentDelegate(argKey: String? = null): LazyProvider<Activity, T> {
    return argumentDelegate({ it.intent?.extras }, argKey)
}

inline fun <reified T> Fragment.argumentDelegate(argKey: String? = null): LazyProvider<Fragment, T> {
    return argumentDelegate({ it.arguments }, argKey)
}
