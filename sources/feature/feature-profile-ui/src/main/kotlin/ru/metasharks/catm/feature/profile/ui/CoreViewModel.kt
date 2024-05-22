package ru.metasharks.catm.feature.profile.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.OfflineSwitcherScreen
import ru.metasharks.catm.core.navigation.screens.SyncScreen
import ru.metasharks.catm.core.navigation.screens.fragments.BriefingsScreen
import ru.metasharks.catm.core.navigation.screens.fragments.DocumentsScreen
import ru.metasharks.catm.core.navigation.screens.fragments.EducationScreen
import ru.metasharks.catm.core.navigation.screens.fragments.FeedbackScreen
import ru.metasharks.catm.core.navigation.screens.fragments.MainScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.bottom.CoreScreenPage
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.repository.SaveRepository
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.profile.role.RoleProvider
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class CoreViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val mainScreen: MainScreen,
    private val briefingsScreen: BriefingsScreen,
    private val documentsScreen: DocumentsScreen,
    private val educationScreen: EducationScreen,
    private val feedbackScreen: FeedbackScreen,
    private val syncScreen: SyncScreen,
    private val offlineModeProvider: OfflineModeProvider,
    private val offlineSwitcherScreen: OfflineSwitcherScreen,
    private val saveRepository: SaveRepository,
    private val roleProvider: RoleProvider,
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _pagesLiveData = MutableLiveData<List<CoreScreenPage>>()
    val pagesLiveData: LiveData<List<CoreScreenPage>> = _pagesLiveData

    private val _isInOfflineLiveData = MutableLiveData<Boolean>()
    val isInOfflineLiveData: LiveData<Boolean> = _isInOfflineLiveData

    private val _roleLiveData = MutableLiveData<Role>()
    val roleLiveData: LiveData<Role> = _roleLiveData

    val isInOffline: Boolean
        get() = isInOfflineLiveData.value ?: false

    private val _savedTypes = MutableLiveData<List<OfflineSave>>()
    val savedTypes: LiveData<List<OfflineSave>> = _savedTypes

    private val onlinePages = listOf(
        CoreScreenPage(PagesInfo.mainScreenPageInfo, screenFactory = { mainScreen() }),
        CoreScreenPage(PagesInfo.briefingsScreenPageInfo, screenFactory = { briefingsScreen() }),
        CoreScreenPage(PagesInfo.trainingsScreenPageInfo, screenFactory = { educationScreen() }),
        CoreScreenPage(PagesInfo.documentsScreenPageInfo, screenFactory = { documentsScreen() }),
        CoreScreenPage(PagesInfo.feedbackScreenPageInfo, screenFactory = { feedbackScreen() }),
    )

    private val offlinePages = listOf(
        CoreScreenPage(PagesInfo.mainScreenPageInfo, screenFactory = { mainScreen() }),
        CoreScreenPage(PagesInfo.briefingsScreenPageInfo, screenFactory = { briefingsScreen() }),
    )

    fun exit() {
        appRouter.exit()
    }

    private fun getSaved() {
        saveRepository.getSaved()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _savedTypes.value = it
                }, {
                    Timber.e(it)
                }
            ).addTo(compositeDisposable)
    }

    private fun getPages() {
        if (isInOffline) {
            _pagesLiveData.value = offlinePages
        } else {
            _pagesLiveData.value = onlinePages
        }
    }

    fun openSyncScreen() {
        appRouter.navigateTo(syncScreen())
    }

    fun openOfflineSwitcherScreen(fromOnline: Boolean, force: Boolean) {
        appRouter.navigateTo(offlineSwitcherScreen(fromOnline = fromOnline, force = force))
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun init() {
        _isInOfflineLiveData.value = offlineModeProvider.isInOfflineMode
        _roleLiveData.value = roleProvider.currentRole
        getPages()
        if (isInOffline) {
            getSaved()
        }
    }
}
