package ru.metasharks.catm.feature.createworkpermit.ui.steps.approval

import android.content.Context
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentStepPatternBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.responsibleworkers.ResponsibleEmployeesViewModel
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

class ApprovalFragment : StepFragment(R.layout.fragment_step_pattern),
    PickItemDialog.Callback {

    private lateinit var toolbarCallback: ToolbarCallback

    private val binding: FragmentStepPatternBinding by viewBinding()

    private val fields = listOf(
        Field.Pick(
            TAG_RESPONSIBLE_MANAGER,
            Field.Pick.Input(
                StringResWrapper(R.string.field_label_responsible_manager_approval),
                StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = {
                    showPickDialog(
                        it,
                        StringResWrapper(R.string.field_label_responsible_manager_approval),
                        TAG_RESPONSIBLE_MANAGER
                    )
                }
            )
        ),
        Field.Pick(
            TAG_ADMITTING_PERSON,
            Field.Pick.Input(
                StringResWrapper(R.string.field_label_admitting_person),
                StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = {
                    showPickDialog(
                        it,
                        StringResWrapper(R.string.field_label_admitting_person),
                        TAG_ADMITTING_PERSON
                    )
                }
            )
        ),
        Field.Pick(
            TAG_INDUSTRY_SAFETY_OFFICER,
            Field.Pick.Input(
                StringResWrapper(R.string.field_label_indusrty_safety_officer),
                StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = {
                    showPickDialog(
                        it,
                        StringResWrapper(R.string.field_label_indusrty_safety_officer),
                        TAG_INDUSTRY_SAFETY_OFFICER
                    )
                }
            )
        ),
        Field.Pick(
            TAG_WORK_SAFETY_OFFICER,
            Field.Pick.Input(
                StringResWrapper(R.string.field_label_work_safety_officer),
                StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = {
                    showPickDialog(
                        it,
                        StringResWrapper(R.string.field_label_work_safety_officer),
                        TAG_WORK_SAFETY_OFFICER
                    )
                }
            )
        ),
        Field.Pick(
            TAG_APPROVER,
            Field.Pick.Input(
                StringResWrapper(R.string.field_label_approver),
                StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = {
                    showPickDialog(
                        it,
                        StringResWrapper(R.string.field_label_approver),
                        TAG_APPROVER
                    )
                }
            )
        ),
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolbarCallback = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()

        setupSteps()
    }

    private fun setupUi() {
        with(binding) {
            nextBtn.setText(R.string.btn_create_label)
            nextBtn.setOnClickListenerAndLoad {
                backBtn.isEnabled = false
                toolbarCallback.showBack(false, null)
                endStep()
            }
            backBtn.setOnClickListener {
                stepCallback.back()
            }
            stepPatterns.setOnFormFilledListener {
                nextBtn.isEnabled = it
            }
        }
    }

    private fun endStep() {
        stepCallback.endStep(
            gatherOutputData(),
            null
        )
        binding.nextBtn
    }

    private fun gatherOutputData(): ApprovalStepOutput {
        val data = binding.stepPatterns.gatherData()
        return ApprovalStepOutput(
            approvalResponsibleManagerId = data[TAG_RESPONSIBLE_MANAGER] as Long,
            admittingPersonId = data[TAG_ADMITTING_PERSON] as Long,
            industrySafetyOfficerId = data[TAG_INDUSTRY_SAFETY_OFFICER] as Long,
            workSafetyOfficerId = data[TAG_WORK_SAFETY_OFFICER] as Long,
            approverId = data[TAG_APPROVER] as Long,
        )
    }

    private fun showPickDialog(view: View, label: StringResWrapper, tag: String) {
        PickItemDialog.Builder(requireContext())
            .setDismissOnPick(true)
            .setTitle(requireContext().getString(label))
            .setInitialItem(view.tag as PickItemDialog.ItemUi?)
            .setViewModelClass(ResponsibleEmployeesViewModel::class.java)
            .build()
            .show(childFragmentManager, tag)
    }

    private fun setupSteps() {
        binding.stepPatterns.inflateOf(
            fields,
            setNecessaryItems = true
        )
    }

    override fun onItemPicked(tag: String, item: PickItemDialog.ItemUi?) {
        binding.stepPatterns.setValueForFieldWithTag(tag, item)
    }

    companion object {

        const val TAG_RESPONSIBLE_MANAGER = "responsible_manager"
        const val TAG_ADMITTING_PERSON = "admitting_person"
        const val TAG_INDUSTRY_SAFETY_OFFICER = "industry_safety_officer"
        const val TAG_WORK_SAFETY_OFFICER = "work_safety_officer"
        const val TAG_APPROVER = "approver"
    }
}
