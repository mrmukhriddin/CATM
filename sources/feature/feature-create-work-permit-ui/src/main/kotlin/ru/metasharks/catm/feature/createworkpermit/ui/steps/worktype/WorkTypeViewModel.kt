package ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler.NumberedItemUi
import javax.inject.Inject

@HiltViewModel
class WorkTypeViewModel @Inject constructor() : ViewModel() {

    private val _workTypes = MutableLiveData<List<BaseListItem>>()
    val workTypes: LiveData<List<BaseListItem>> = _workTypes

    fun loadWorkTypes() {
        _workTypes.value = listOf(
            NumberedItemUi(
                1, 1, "Работы на высоте", "Работы на высоте"
            )
        )
    }
}
