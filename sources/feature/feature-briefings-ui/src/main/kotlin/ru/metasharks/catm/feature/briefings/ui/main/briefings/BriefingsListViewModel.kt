package ru.metasharks.catm.feature.briefings.ui.main.briefings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import ru.metasharks.catm.feature.briefings.ui.filters.FilterBriefingItems
import javax.inject.Inject

@HiltViewModel
internal class BriefingsListViewModel @Inject constructor(
    private val filter: FilterBriefingItems,
) : ViewModel() {

    private val _filteredBriefings = MutableLiveData<List<BriefingUi>>()
    val filteredBriefings: LiveData<List<BriefingUi>> = _filteredBriefings

    private var categoryId: Int = 0
    private var typeId: Int = 0

    fun initialData(categoryId: Int, typeId: Int) {
        this.categoryId = categoryId
        this.typeId = typeId
    }

    fun filterBriefings(briefings: List<BriefingUi>) {
        _filteredBriefings.value = filter(briefings, categoryId, typeId)
    }
}
