package ru.metasharks.catm.feature.createworkpermit.ui.steps

import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.metasharks.catm.feature.createworkpermit.ui.steps.approval.ApprovalFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.approval.ApprovalStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment.AttachmentFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment.AttachmentStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors.DangerousFactorsFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors.DangerousFactorsStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeesFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeesStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.equipment.EquipmentFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.equipment.EquipmentStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo.GeneraInfoFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo.GeneralInfoStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.workscheme.WorkSchemeFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.workscheme.WorkSchemeStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.WorkTypeFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.WorkTypeStep
import ru.metasharks.catm.step.ProcessManager
import ru.metasharks.catm.step.entities.RestoreData

class CreateWorkPermitProcessManager(
    fragmentManager: FragmentManager,
    fragmentContainer: FrameLayout,
    stepDescriptionTextView: TextView,
    onEndCallback: () -> Unit
) : ProcessManager(
    fragmentManager, fragmentContainer, stepDescriptionTextView, onEndCallback
) {

    private var workType: String? = null

    override fun fragmentFactory(tag: String, restoreData: RestoreData?): Fragment {
        return when (tag) {
            WorkTypeStep.TAG -> WorkTypeFragment()
            GeneralInfoStep.TAG -> GeneraInfoFragment.newInstance(
                restoreData,
                requireNotNull(workType)
            )
            DangerousFactorsStep.TAG -> DangerousFactorsFragment.newInstance(restoreData)
            EquipmentStep.TAG -> EquipmentFragment.newInstance(restoreData)
            WorkSchemeStep.TAG -> WorkSchemeFragment.newInstance(restoreData)
            EmployeesStep.TAG -> EmployeesFragment.newInstance(restoreData)
            AttachmentStep.TAG -> AttachmentFragment.newInstance(restoreData)
            ApprovalStep.TAG -> ApprovalFragment()
            else -> {
                throw IllegalArgumentException("Unknown tag")
            }
        }
    }

    fun setWorkType(workType: String) {
        this.workType = workType
    }
}
