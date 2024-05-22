package ru.metasharks.catm.core.ui.activity

interface ToolbarCallback {

    fun showBack(show: Boolean, onBackPressedAction: (() -> Unit)?)
}
