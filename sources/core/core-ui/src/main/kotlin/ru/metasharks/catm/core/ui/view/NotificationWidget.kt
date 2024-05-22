package ru.metasharks.catm.core.ui.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.NotificationsScreen
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.databinding.WidgetNotificationBinding
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.setDefinitePadding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val prefSubject: PublishSubject<Boolean> = PublishSubject.create()

    private var preferencesDisposable: Disposable? = null

    private var hasUnreadNotifications: Boolean = false

    @Inject
    lateinit var appRouter: ApplicationRouter

    @Inject
    lateinit var notificationsScreen: NotificationsScreen

    @Inject
    lateinit var preferencesProvider: PreferencesProvider

    private val hasUnreadNotificationsDefault: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        context.getString(ru.metasharks.catm.core.storage.R.string.preferences_key_has_unread_notifications),
        false
    )

    private val onSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == context.getString(ru.metasharks.catm.core.storage.R.string.preferences_key_has_unread_notifications)) {
                prefSubject.onNext(sharedPreferences.getBoolean(key, false))
            }
        }

    private val circleRadius: Float by lazy {
        context.dpToPx(NOTIFICATION_RADIUS_DP).toFloat()
    }

    private val circlePaint: Paint by lazy {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.yellow)
        paint
    }

    private val binding: WidgetNotificationBinding by lazy {
        WidgetNotificationBinding.bind(this)
    }

    init {
        inflate(context, R.layout.widget_notification, null)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            typedValue,
            true
        )
        binding.root.setBackgroundResource(typedValue.resourceId)
        binding.root.setImageResource(R.drawable.ic_bell)
        binding.root.setDefinitePadding(
            right = context.dpToPx(NOTIFICATION_RADIUS_DP),
            top = context.dpToPx(NOTIFICATION_RADIUS_DP),
            bottom = context.dpToPx(NOTIFICATION_RADIUS_DP),
        )
        binding.root.setOnClickListener {
            appRouter.navigateTo(notificationsScreen())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hasUnreadNotifications = hasUnreadNotificationsDefault
        preferencesProvider.applicationPreferences.registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
        preferencesDisposable = prefSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    hasUnreadNotifications = it
                    invalidate()
                }, {
                    Timber.e(it)
                }
            )
    }

    fun refresh() {
        prefSubject.onNext(hasUnreadNotificationsDefault)
    }

    override fun onDetachedFromWindow() {
        preferencesProvider.applicationPreferences.unregisterOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
        preferencesDisposable?.dispose()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hasUnreadNotifications) {
            val width = width.toFloat()
            canvas.drawCircle(
                width - circleRadius, circleRadius,
                circleRadius,
                circlePaint
            )
        }
    }

    companion object {

        private const val NOTIFICATION_RADIUS_DP = 4
    }
}
