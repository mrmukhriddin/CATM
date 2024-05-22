package ru.metasharks.catm.feature.notifications.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.BriefingDetailsScreen
import ru.metasharks.catm.core.navigation.screens.CurrentProfileScreen
import ru.metasharks.catm.core.navigation.screens.ProfileScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsScreen
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.notifications.repository.NotificationsRepository
import ru.metasharks.catm.feature.notifications.ui.recycler.NotificationRecyclerMapper
import javax.inject.Inject

@HiltViewModel
internal class NotificationsViewModel @Inject constructor(
    private val applicationRouter: ApplicationRouter,
    private val notificationsRepository: NotificationsRepository,
    private val mapper: NotificationRecyclerMapper,
    private val workPermitDetailsScreen: WorkPermitDetailsScreen,
    private val briefingDetailsScreen: BriefingDetailsScreen,
    private val currentProfileScreen: CurrentProfileScreen,
    private val profileScreen: ProfileScreen,
) : ViewModel() {

    private var notificationsDisposable: Disposable? = null

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _adapterItems = MutableLiveData<List<BaseListItem>>()
    val adapterItems: LiveData<List<BaseListItem>> = _adapterItems

    fun readNotification(notificationId: Long) {
        notificationsRepository.readNotification(notificationId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun readAllNotifications() {
        notificationsRepository.readAllNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun loadNotifications() {
        notificationsDisposable = notificationsRepository.getNotifications()
            .map { notifications ->
                notifications.map { mapper.mapNotificationToUi(it.content) }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    _adapterItems.value = items
                }, { error ->
                    _error.value = error
                }
            )
    }

    fun exit() {
        applicationRouter.exit()
    }

    fun openWorkPermit(workPermitId: Long) {
        applicationRouter.navigateTo(workPermitDetailsScreen(workPermitId))
    }

    fun openBriefing(briefingId: Long) {
        applicationRouter.navigateTo(briefingDetailsScreen(briefingId.toInt()))
    }

    fun openCurrentUserProfile() {
        applicationRouter.navigateTo(currentProfileScreen())
    }

    fun openUserProfile(userId: Long) {
        applicationRouter.navigateTo(profileScreen(userId.toInt()))
    }

    override fun onCleared() {
        notificationsDisposable?.dispose()
        super.onCleared()
    }
}
