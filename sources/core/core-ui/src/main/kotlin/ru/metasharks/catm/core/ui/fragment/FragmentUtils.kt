package ru.metasharks.catm.core.ui.fragment

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.requireListener(): T {
    var currentFragment: Fragment = this
    while (currentFragment.parentFragment != null) {
        if (currentFragment.parentFragment is T) {
            return currentFragment.parentFragment as T
        }
        currentFragment = currentFragment.requireParentFragment()
    }
    return activity as T
}

inline fun <reified T> Fragment.findListener(): T? {
    var currentFragment: Fragment = this
    while (currentFragment.parentFragment != null) {
        if (currentFragment.parentFragment is T) {
            return currentFragment.parentFragment as T
        }
        currentFragment = currentFragment.requireParentFragment()
    }
    return activity as? T
}
