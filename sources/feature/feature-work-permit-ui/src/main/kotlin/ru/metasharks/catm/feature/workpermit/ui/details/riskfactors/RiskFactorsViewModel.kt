package ru.metasharks.catm.feature.workpermit.ui.details.riskfactors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.feature.workpermit.ui.mapper.RiskFactorsMapper
import ru.metasharks.catm.feature.workpermit.usecase.details.GetRiskFactorsUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class RiskFactorsViewModel @Inject constructor(
    private val getRiskFactorsUseCase: GetRiskFactorsUseCase,
    private val mapper: RiskFactorsMapper,
    private val appRouter: ApplicationRouter,
) : ViewModel() {

    private val _stages = MutableLiveData<List<RiskFactorStageUi>>()
    val stages: LiveData<List<RiskFactorStageUi>> = _stages

    fun init() {
        getRiskFactorsUseCase()
            .map { stages ->
                mapper.mapStages(stages)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _stages.value = it
                }, {
                    Timber.e(it)
                }
            )
    }

    fun exit() {
        appRouter.exit()
    }
}
