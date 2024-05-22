package ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.utils.getShortFormOfFullName
import ru.metasharks.catm.feature.offline.save.workpermit.GetWorkPermitCreateOfflineToolsUseCase
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetOrganizationUseCase
import ru.metasharks.catm.step.Field
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GeneralInfoViewModel @Inject constructor(
    private val getCurrentProfile: GetCurrentUserUseCase,
    private val getOrganizationUseCase: GetOrganizationUseCase,
    private val offlineModeProvider: OfflineModeProvider,
    private val getWorkPermitCreateOfflineToolsUseCase: GetWorkPermitCreateOfflineToolsUseCase,
) : ViewModel() {

    private val _initialData = MutableLiveData<Payload>()
    val initialData: LiveData<Payload> = _initialData

    fun getInitialDataForFields() {
        getCurrentProfile(false)
            .map { (_, profile) ->
                Field.Pick.InitialValue(
                    PickItemDialog.ItemUi(
                        entityId = profile.id,
                        value = getShortFormOfFullName(
                            profile.lastName,
                            profile.firstName,
                            profile.middleName
                        )
                    )
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _initialData.value = Payload(RESPONSIBLE, it)
                }, {
                    Timber.e(it)
                }
            )
    }

    fun getOrganization() {
        if (offlineModeProvider.isInOnlineMode) {
            getOrganizationUseCase()
        } else {
            getWorkPermitCreateOfflineToolsUseCase()
                .map { it.organization }.toSingle()
        }
            .map { it.name }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { organizationName ->
                    _initialData.value = Payload(ORGANIZATION, organizationName)
                }, {
                    Timber.e(it)
                }
            )
    }

    class Payload(
        val tag: String,
        val initialValue: Any,
    )

    companion object {
        const val ORGANIZATION = "organization"
        const val RESPONSIBLE = "responsible"
    }
}
