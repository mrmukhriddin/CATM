package ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentStepPatternBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.responsibleworkers.ResponsibleEmployeesViewModel
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.step.entities.StepPatternRestoreData
import ru.metasharks.catm.step.validator.DateAfterTodayValidator
import ru.metasharks.catm.step.validator.DateValidator
import ru.metasharks.catm.step.validator.TwoSubsequentDatesValidator
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

@AndroidEntryPoint
internal class GeneraInfoFragment : StepFragment(R.layout.fragment_step_pattern),
    PickItemDialog.Callback {

    private val binding: FragmentStepPatternBinding by viewBinding()
    private val viewModel: GeneralInfoViewModel by viewModels()

    private var currentSnackbar: CustomSnackbar? = null

    private val restoreData: StepPatternRestoreData? by argumentDelegate(ARG_RESTORE_DATA)
    private val workType: String by argumentDelegate(ARG_WORK_TYPE)
    private lateinit var initValue: Field.Pick.InitialValue

    private val fieldList by lazy {
        listOf(
            Field.Text(
                TAG_ORGANIZATION,
                Field.Text.Input(
                    StringResWrapper(R.string.field_label_organization),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input),
                    isEditable = false,
                    initialValue = restoreData?.byTag(TAG_ORGANIZATION) ?: run {
                        viewModel.getOrganization()
                        null
                    }
                )
            ),
            Field.Text(
                TAG_WORK_TYPE,
                Field.Text.Input(
                    StringResWrapper(R.string.field_label_work_type),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input),
                    initialValue = workType,
                    isEditable = false,
                    clarification = StringResWrapper(R.string.field_clarification_work_type)
                )
            ),
            Field.Text(
                TAG_PLACE,
                Field.Text.Input(
                    StringResWrapper(R.string.field_label_place),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input),
                    oneLine = false,
                    clarification = StringResWrapper(R.string.field_clarification_place),
                    initialValue = restoreData?.byTag(TAG_PLACE)
                )
            ),
            Field.DateAndTime(
                TAG_START_TIME,
                Field.DateAndTime.Input(
                    StringResWrapper(R.string.field_label_start_time),
                    initialValue = restoreData?.byTag(TAG_START_TIME)
                )
            ),
            Field.DateAndTime(
                TAG_END_TIME,
                Field.DateAndTime.Input(
                    StringResWrapper(R.string.field_label_end_time),
                    initialValue = restoreData?.byTag(TAG_END_TIME)
                )
            ),
            Field.Radio(
                TAG_SHIFT,
                Field.Radio.Input(
                    StringResWrapper(R.string.field_label_shift),
                    listOf(
                        StringResWrapper(R.string.field_radio_shift_day) to SHIFT_DAY,
                        StringResWrapper(R.string.field_radio_shift_night) to SHIFT_NIGHT,
                    ),
                    initialValue = restoreData?.byTag(TAG_SHIFT) ?: SHIFT_DAY
                )
            ),
            Field.Pick(
                TAG_RESPONSIBLE_MANAGER,
                Field.Pick.Input(
                    StringResWrapper(R.string.field_label_responsible_manager),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                    initialValue = restoreData?.byTag(TAG_RESPONSIBLE_MANAGER) ?: initValue,
                    onSelectCallback = {
                        showPickDialog(
                            it,
                            StringResWrapper(R.string.field_label_responsible_manager),
                            TAG_RESPONSIBLE_MANAGER
                        )
                    },
                    isEditable = false
                )
            ),
            Field.Pick(
                TAG_EXECUTOR,
                Field.Pick.Input(
                    StringResWrapper(R.string.field_label_executor),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                    initialValue = restoreData?.byTag(TAG_EXECUTOR),
                    onSelectCallback = {
                        showPickDialog(
                            it,
                            StringResWrapper(R.string.field_label_executor),
                            TAG_EXECUTOR
                        )
                    }
                )
            ),
            Field.Pick(
                TAG_PERMIT_ISSUER,
                Field.Pick.Input(
                    StringResWrapper(R.string.field_label_permit_issuer),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                    initialValue = restoreData?.byTag(TAG_PERMIT_ISSUER),
                    onSelectCallback = {
                        showPickDialog(
                            it,
                            StringResWrapper(R.string.field_label_permit_issuer),
                            TAG_PERMIT_ISSUER
                        )
                    }
                )
            ),
            Field.Pick(
                TAG_PERMIT_ACCEPTOR,
                Field.Pick.Input(
                    StringResWrapper(R.string.field_label_permit_acceptor),
                    StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                    initialValue = restoreData?.byTag(TAG_PERMIT_ACCEPTOR),
                    onSelectCallback = {
                        showPickDialog(
                            it,
                            StringResWrapper(R.string.field_label_permit_acceptor),
                            TAG_PERMIT_ACCEPTOR
                        )
                    }
                )
            ),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.getInitialDataForFields()
    }

    private fun setupObservers() {
        viewModel.initialData.observe(viewLifecycleOwner) { initValue ->
            when (initValue.tag) {
                GeneralInfoViewModel.RESPONSIBLE -> setupSteps(initValue.initialValue as Field.Pick.InitialValue)
                GeneralInfoViewModel.ORGANIZATION -> {
                    val organizationName = initValue.initialValue as String
                    if ((binding.stepPatterns.gatherDataFromField(TAG_ORGANIZATION) as String?).isNullOrBlank()) {
                        binding.stepPatterns.setValueForFieldWithTag(
                            TAG_ORGANIZATION,
                            organizationName
                        )
                    }
                }
            }
        }
    }

    private fun showPickDialog(view: View, label: StringResWrapper, tag: String) {
        PickItemDialog.Builder(requireContext())
            .setDismissOnPick(true)
            .setTitle(view.context.getString(label))
            .setInitialItem(view.tag as PickItemDialog.ItemUi?)
            .setViewModelClass(ResponsibleEmployeesViewModel::class.java)
            .build()
            .show(childFragmentManager, tag)
    }

    private fun setupSteps(initValue: Field.Pick.InitialValue) {
        this.initValue = initValue
        binding.stepPatterns.setNecessaryTags(
            setOf(
                TAG_ORGANIZATION,
                TAG_PLACE,
                TAG_START_TIME,
                TAG_END_TIME,
                TAG_SHIFT,
                TAG_RESPONSIBLE_MANAGER,
                TAG_EXECUTOR,
                TAG_PERMIT_ISSUER,
                TAG_PERMIT_ACCEPTOR
            )
        )
        binding.stepPatterns.addValidators(
            DateValidator(TAG_START_TIME, StringResWrapper(R.string.warning_date_wrong_format)),
            DateAfterTodayValidator(TAG_START_TIME, StringResWrapper(R.string.warning_date_passed)),
            DateValidator(TAG_END_TIME, StringResWrapper(R.string.warning_date_wrong_format)),
            TwoSubsequentDatesValidator(
                TAG_START_TIME,
                TAG_END_TIME,
                warning = StringResWrapper(R.string.warning_date_mismatch)
            )
        )
        binding.stepPatterns.inflateOf(fieldList)
    }

    private fun setupUi() {
        binding.backBtn.isGone = true
        binding.stepPatterns.setOnFormFilledListener { formFilled ->
            binding.nextBtn.isEnabled = formFilled
        }
        binding.nextBtn.setOnClickListener {
            val validation = binding.stepPatterns.validate()
            if (validation.isValid) {
                val data = binding.stepPatterns.gatherData()
                val restoreData = StepPatternRestoreData(binding.stepPatterns.gatherRestoreData())
                stepCallback.endStep(dataToOutput(data), restoreData)
            }
        }
    }

    private fun showValidationFailedWarning(warning: String) {
        currentSnackbar = CustomSnackbar.make(
            binding.nextBtn, warning, Snackbar.LENGTH_INDEFINITE
        )
        currentSnackbar?.show()
    }

    private fun dataToOutput(data: MutableMap<String, Any?>): GeneralInfoStepOutput {
        return GeneralInfoStepOutput(
            organization = data[TAG_ORGANIZATION] as String,
            place = data[TAG_PLACE] as String,
            startTime = data[TAG_START_TIME] as String,
            expirationTime = data[TAG_END_TIME] as String,
            shift = data[TAG_SHIFT] as String,
            responsibleManagerId = data[TAG_RESPONSIBLE_MANAGER] as Long,
            responsibleExecutorId = data[TAG_EXECUTOR] as Long,
            permitIssuerId = data[TAG_PERMIT_ISSUER] as Long,
            permitAcceptorId = data[TAG_PERMIT_ACCEPTOR] as Long,
        )
    }

    override fun onItemPicked(tag: String, item: PickItemDialog.ItemUi?) {
        binding.stepPatterns.setValueForFieldWithTag(tag, item)
    }

    override fun onKeyboardShown() {
        binding.buttonsContainer.isGone = true
    }

    override fun onKeyboardHidden() {
        binding.buttonsContainer.isVisible = true
    }

    override fun onDestroy() {
        currentSnackbar?.dismiss()
        currentSnackbar = null
        super.onDestroy()
    }

    companion object {

        private const val ARG_RESTORE_DATA = "restore_data"
        private const val ARG_WORK_TYPE = "work_type"

        fun newInstance(restoreData: RestoreData?, workType: String): Fragment {
            val fragment = GeneraInfoFragment()
            fragment.arguments = bundleOf(
                ARG_RESTORE_DATA to restoreData,
                ARG_WORK_TYPE to workType
            )
            return fragment
        }

        private const val TAG_ORGANIZATION = "organization"
        private const val TAG_WORK_TYPE = "work_type"
        private const val TAG_PLACE = "place"
        private const val TAG_START_TIME = "start_time"
        private const val TAG_END_TIME = "end_time"
        private const val TAG_SHIFT = "shift"
        private const val TAG_RESPONSIBLE_MANAGER = "responsible_manager"
        private const val TAG_EXECUTOR = "executor"
        private const val TAG_PERMIT_ISSUER = "permit_issuer"
        private const val TAG_PERMIT_ACCEPTOR = "permit_acceptor"

        private const val SHIFT_DAY = "day"
        private const val SHIFT_NIGHT = "night"
    }
}
