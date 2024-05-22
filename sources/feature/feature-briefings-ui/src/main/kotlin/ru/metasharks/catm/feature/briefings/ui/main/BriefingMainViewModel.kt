package ru.metasharks.catm.feature.briefings.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.BriefingDetailsScreen
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.briefings.ui.ChipFactory
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingCategoryUi
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import ru.metasharks.catm.feature.briefings.ui.mappers.BriefingCategoryMapper
import ru.metasharks.catm.feature.briefings.ui.mappers.BriefingMapper
import ru.metasharks.catm.feature.briefings.ui.mappers.BriefingTypeMapper
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingCategoriesScreen
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingTypesScreen
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingsListScreen
import ru.metasharks.catm.feature.briefings.usecase.GetMainBriefingDataUseCase
import ru.metasharks.catm.feature.offline.save.briefings.GetBriefingOfflineDataUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class BriefingMainViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val briefingCategoriesScreen: BriefingCategoriesScreen,
    private val briefingTypesScreen: BriefingTypesScreen,
    private val briefingsListScreen: BriefingsListScreen,
    private val briefingDetailsScreen: BriefingDetailsScreen,
    private val getMainBriefingDataUseCase: GetMainBriefingDataUseCase,
    private val briefingCategoryMapper: BriefingCategoryMapper,
    private val briefingTypeMapper: BriefingTypeMapper,
    private val briefingMapper: BriefingMapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val getBriefingOfflineData: GetBriefingOfflineDataUseCase,
) : ViewModel() {

    internal lateinit var innerRouter: Router

    private val _briefingCategories = MutableLiveData<List<BriefingCategoryUi>>()
    val briefingCategories: LiveData<List<BriefingCategoryUi>> = _briefingCategories

    private val _briefingTypes = MutableLiveData<List<BriefingTypeUi>>()
    val briefingTypes: LiveData<List<BriefingTypeUi>> = _briefingTypes

    private val _briefings = MutableLiveData<List<BriefingUi>>()
    val briefings: LiveData<List<BriefingUi>> = _briefings

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isInOfflineModeLiveData = MutableLiveData<Boolean>()
    val isInOfflineModeLiveData: LiveData<Boolean> = _isInOfflineModeLiveData

    val isInOffline: Boolean
        get() = isInOfflineModeLiveData.value ?: false

    fun init() {
        _isInOfflineModeLiveData.value = offlineModeProvider.isInOfflineMode
        if (isInOffline) {
            loadOfflineData()
        } else {
            loadOnlineData()
        }
    }

    private fun loadOfflineData() {
        getBriefingOfflineData()
            .map { data ->
                val briefings = data.briefings.map { it.briefingX }
                Triple(
                    briefingCategoryMapper.map(data.categories),
                    briefingTypeMapper.map(data.types, briefings),
                    briefingMapper.map(briefings),
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onSuccess, ::onError)
    }

    fun loadOnlineData() {
        _dataLoading.value = true
        getMainBriefingDataUseCase()
            .map { data ->
                Triple(
                    briefingCategoryMapper.map(data.categories),
                    briefingTypeMapper.map(data.types, data.briefings),
                    briefingMapper.map(data.briefings),
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _dataLoading.value = false }
            .subscribe(::onSuccess, ::onError)
    }

    private fun onSuccess(triple: Triple<List<BriefingCategoryUi>, List<BriefingTypeUi>, List<BriefingUi>>) {
        val (categories, types, briefings) = triple
        _briefingCategories.value = categories.filter { category ->
            briefings.map { it.categoryId }.contains(category.categoryId)
        }
        _briefingTypes.value = types
        _briefings.value = briefings
    }

    private fun onError(throwable: Throwable) {
        Timber.e(throwable)
    }

    // level 1
    fun openBriefingCategoriesScreen() {
        innerRouter.navigateTo(briefingCategoriesScreen())
    }

    // level 2
    fun openBriefingTypesScreen(categoryId: Int) {
        innerRouter.navigateTo(briefingTypesScreen(categoryId))
    }

    // level 3
    fun openBriefingsListScreen(categoryId: Int, typeId: Int) {
        innerRouter.navigateTo(briefingsListScreen(categoryId, typeId))
    }

    // level 4
    fun openBriefingDetails(briefing: BriefingUi) {
        appRouter.navigateToWithResult<Int>(briefingDetailsScreen(briefing.brId)) { signedBriefingId ->
            Timber.d(signedBriefingId.toString())
            val newList = _briefings.value.orEmpty().toMutableList()
            val index = newList.indexOfFirst { it.brId == signedBriefingId }
            newList[index] =
                newList[index].copy(
                    statusChip = briefingMapper.getChipItem(ChipFactory.Type.PASSED),
                    chipType = ChipFactory.Type.PASSED,
                )
            _briefings.value = newList
            if (newList.all { it.chipType == ChipFactory.Type.PASSED }) {
                val newTypes = _briefingTypes.value.orEmpty().toMutableList()
                val typeIndex = newTypes.indexOfFirst { it.typeId == briefing.typeId }
                newTypes[typeIndex] =
                    newTypes[typeIndex].copy(
                        chipItem = briefingTypeMapper.getChipItem(ChipFactory.Type.PASSED),
                        chipType = ChipFactory.Type.PASSED,
                    )
                _briefingTypes.value = newTypes
            }
        }
    }
}
