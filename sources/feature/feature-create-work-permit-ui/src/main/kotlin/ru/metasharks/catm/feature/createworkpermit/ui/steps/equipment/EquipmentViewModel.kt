package ru.metasharks.catm.feature.createworkpermit.ui.steps.equipment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepMapper
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.workpermit.entities.options.OptionsEnvelopeX
import ru.metasharks.catm.feature.workpermit.usecase.GetOptionsUseCase
import ru.metasharks.catm.step.StepPatternLayout
import ru.metasharks.catm.step.entities.StepPatternRestoreData
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val getOptionsUseCase: GetOptionsUseCase,
    private val mapper: StepMapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
) : ViewModel() {

    private val _createData = MutableLiveData<List<StepPatternLayout.CreateItemData>>()
    val createData: LiveData<List<StepPatternLayout.CreateItemData>> = _createData

    private fun optionsSource(): Single<OptionsEnvelopeX> {
        return if (offlineModeProvider.isInOnlineMode) {
            getOptionsUseCase()
        } else {
            getWorkPermitCreateOfflineToolsUseCase().map { it.allOptions }.toSingle()
        }
    }

    fun load(restoreData: StepPatternRestoreData?) {
        optionsSource()
            .map { mapper.mapOptionsToUi(it.usedEquipment, restoreData = restoreData) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _createData.value = it
                }, {
                    Timber.e(it)
                }
            )
    }
}