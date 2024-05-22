package ru.metasharks.catm.feature.workpermit.ui.filter

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.joda.time.LocalDate
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData
import javax.inject.Inject

@HiltViewModel
internal class WorkPermitsFilterViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
) : ViewModel() {

    var filterOutput: FilterData? = null
        set(value) {
            if (value == field) {
                return
            }
            field = value
            val currentCodeToIntent = requireNotNull(_resultIntent.value)
            val currentCode = currentCodeToIntent.first
            val currentIntent = currentCodeToIntent.second ?: Intent()
            currentIntent.putExtra(GetFilters.EXTRA_FILTER_OUTPUT, field)
            _resultIntent.value = currentCode to currentIntent
        }

    private val currentFilterOutput: FilterData
        get() {
            if (filterOutput == null) {
                filterOutput = FilterData()
            }
            return requireNotNull(filterOutput)
        }

    private val _resultIntent =
        MutableLiveData<Pair<Int, Intent?>>(Activity.RESULT_CANCELED to null)
    val resultIntent: LiveData<Pair<Int, Intent?>> = _resultIntent

    fun exit() {
        appRouter.exit()
    }

    fun saveResultAndExit() {
        val currentIntent = resultIntent.value?.second ?: Intent()
        _resultIntent.value = Activity.RESULT_OK to currentIntent
        exit()
    }

    fun setDate(first: LocalDate, second: LocalDate) {
        filterOutput = currentFilterOutput.copy(createdAfter = first, createdBefore = second)
    }

    fun setResponsibleManager(managerId: Long?, name: String?) {
        filterOutput = currentFilterOutput.copy(
            responsibleManagerId = managerId,
            responsibleManagerName = name
        )
    }

    fun setWorkType(workTypeId: Long?, name: String?) {
        filterOutput = currentFilterOutput.copy(
            workTypeId = workTypeId,
            workTypeName = name,
        )
    }

    fun setNeedSign(checked: Boolean) {
        filterOutput = currentFilterOutput.copy(
            needSign = if (checked) {
                true
            } else {
                null
            }
        )
    }

    fun reset() {
        filterOutput = null
    }

    fun setInitFilter(filterData: FilterData?) {
        this.filterOutput = filterData
    }
}
