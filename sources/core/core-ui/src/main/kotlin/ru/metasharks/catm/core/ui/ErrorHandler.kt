package ru.metasharks.catm.core.ui

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.AuthAfterTokenExpiredScreen
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val authScreen: AuthAfterTokenExpiredScreen,
    @ApplicationContext private val context: Context,
) {

    fun handle(view: View, error: Throwable) {
        var message = context.getString(R.string.error_during_action_default)
        (error as? HttpException)?.let {
            if (it.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                appRouter.newRootScreen(authScreen())
                return
            }
            val errorJson = it.response()?.errorBody()?.string() ?: return@let
            val json = try {
                JSONObject(errorJson)
            } catch (jsonException: JSONException) {
                return@let
            }
            if (json.keys().hasNext()) {
                val key = json.keys().next()
                message = when (val value = json[key]) {
                    is JSONArray -> {
                        check(value.length() != 0) { "Empty response Array" }
                        value.getString(0)
                    }
                    else -> json.getString(key)
                }
            }
        }
        Timber.e(error)
        CustomSnackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show()
    }

    companion object {

        private const val FIELD_DETAIL = "detail"

        fun getDetailMessageFromHttpException(exception: Throwable): String? {
            if (exception !is HttpException) {
                return null
            }
            val jsonResponseObject = exception.response()?.errorBody()?.string()?.let {
                try {
                    JSONObject(it)
                } catch (ex: JSONException) {
                    return null
                }
            } ?: return null
            return jsonResponseObject[FIELD_DETAIL] as String
        }
    }
}
