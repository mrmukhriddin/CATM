package ru.metasharks.catm.feature.briefings.ui.main.types

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import ru.metasharks.catm.feature.briefings.ui.filters.FilterBriefingTypes
import javax.inject.Inject

@HiltViewModel
internal class BriefingTypesViewModel @Inject constructor(
    private val filter: FilterBriefingTypes,
) : ViewModel() {

    private val _filteredBriefingTypes = MutableLiveData<List<BriefingTypeUi>>()
    val filteredBriefingTypes: LiveData<List<BriefingTypeUi>> = _filteredBriefingTypes

    private var categoryId: Int = 0
    private var briefings: List<BriefingUi>? = null
    private var allTypes: List<BriefingTypeUi>? = null

    fun initialData(categoryId: Int) {
        this.categoryId = categoryId
    }

    fun loadTypes(briefings: List<BriefingUi>? = null, types: List<BriefingTypeUi>? = null) {
        briefings?.let { this.briefings = it }
        types?.let { this.allTypes = it }
        if (this.briefings != null && this.allTypes != null) {
            _filteredBriefingTypes.value = filter(this.briefings!!, this.allTypes!!, categoryId)
        }
    }
}
