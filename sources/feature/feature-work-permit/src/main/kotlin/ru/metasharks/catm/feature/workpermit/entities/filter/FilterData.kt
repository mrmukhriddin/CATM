package ru.metasharks.catm.feature.workpermit.entities.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate

@Parcelize
data class FilterData(
    val createdAfter: LocalDate? = null,
    val createdBefore: LocalDate? = null,
    val responsibleManagerId: Long? = null,
    val responsibleManagerName: String? = null,
    val workTypeId: Long? = null,
    val workTypeName: String? = null,
    val needSign: Boolean? = null,
) : Parcelable {

    fun checkIfAllFieldsNull(): Boolean {
        return this == EMPTY_OBJECT
    }

    companion object {

        val EMPTY_OBJECT = FilterData()

        fun FilterData?.isSameTo(filterData: FilterData?): Boolean {
            return if (this == null && filterData == null) {
                true
            } else if (this == null) {
                requireNotNull(filterData).checkIfAllFieldsNull()
            } else if (filterData == null) {
                checkIfAllFieldsNull()
            } else {
                this == filterData
            }
        }

        fun FilterData?.isEmpty(): Boolean {
            return this == null || checkIfAllFieldsNull()
        }

        fun FilterData?.countNonNullItemsCount(): Int {
            var counter = 0
            if (this == null) {
                return counter
            }
            createdAfter?.let { counter++ }
            responsibleManagerId?.let { counter++ }
            workTypeId?.let { counter++ }
            needSign?.let { counter++ }
            return counter
        }
    }
}
