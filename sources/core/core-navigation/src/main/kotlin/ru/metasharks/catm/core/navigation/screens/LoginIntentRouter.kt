package ru.metasharks.catm.core.navigation.screens

import android.content.Context
import android.content.Intent

fun interface LoginIntentRouter {

    fun createIntent(context: Context, isUserSession: Boolean): Intent
}
