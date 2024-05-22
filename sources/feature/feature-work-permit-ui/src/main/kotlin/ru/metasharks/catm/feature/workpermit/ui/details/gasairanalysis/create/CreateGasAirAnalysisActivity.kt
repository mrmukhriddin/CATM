package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityCreateGasAirAnalysisBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.types.date.DateManager
import ru.metasharks.catm.step.validator.DateBetweenTwoDatesValidator
import ru.metasharks.catm.step.validator.DateValidator
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

@AndroidEntryPoint
internal class CreateGasAirAnalysisActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val binding: ActivityCreateGasAirAnalysisBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: CreateGasAirAnalysisViewModel by viewModels()

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private var fields: List<Field<out Any>> = emptyList()

    private fun getFields(
        device: Pair<String, String>?,
        probeNextDate: String?
    ): List<Field<out Any>> {
        return listOf(
            Field.DateAndTime(
                tag = TAG_PROBE_DATE_TIME,
                input = Field.DateAndTime.Input(
                    label = StringResWrapper(R.string.field_label_probe_date_and_time)
                )
            ),
            Field.Text(
                tag = TAG_PROBE_PLACE,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_probe_place),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.Text(
                tag = TAG_PROBE_COMPONENTS,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_probe_components),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.Text(
                tag = TAG_PROBE_PERMISSIBLE_CONCENTRATION,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_probe_permissible_concentration),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.Text(
                tag = TAG_PROBE_CONCENTRATION,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_probe_concentration),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.DoubleText(
                tag = TAG_PROBE_DEVICE,
                input = Field.DoubleText.Input(
                    label = StringResWrapper(R.string.field_label_probe_device),
                    hintFirst = StringResWrapper(R.string.field_hint_probe_device_model),
                    hintSecond = StringResWrapper(R.string.field_hint_probe_device_number),
                    initialValue = device,
                    isEditable = device == null
                ),
            ),
            Field.Date(
                tag = TAG_PROBE_NEXT_DATE,
                input = Field.Date.Input(
                    label = StringResWrapper(R.string.field_label_probe_date_next),
                    initialValue = probeNextDate,
                    canEditDate = probeNextDate == null
                ),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId, forceRefresh = false)
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            errorHandler.handle(binding.createAnalysisBtn, error)
        }
        viewModel.workPermit.observe(this) { workPermit ->
            val mainInfo = workPermit.mainInfo
            val additionalInfo = (workPermit.additionalInfo as SignedAdditionalInfo)
            val firstGasAirAnalysisData = additionalInfo.gasAnalysisList.firstOrNull()
            with(binding) {
                gaaWorkStart.text = getHighlightedString(
                    this@CreateGasAirAnalysisActivity,
                    R.string.dp_work_permit_start,
                    LocalDateUtils.toString(mainInfo.startDate, mainInfo.startTime)
                )
                gaaWorkEnd.text = getHighlightedString(
                    this@CreateGasAirAnalysisActivity,
                    R.string.dp_work_permit_end,
                    LocalDateUtils.toString(mainInfo.endDate, mainInfo.endTime)
                )
            }
            fields = getFields(
                firstGasAirAnalysisData?.let { it.probeDeviceModel to it.probeDeviceNumber },
                firstGasAirAnalysisData?.let { LocalDateUtils.toString(it.probeNext) }
            )
            setupSteps(workPermit)
        }
    }

    private fun setupUi() {
        with(binding) {
            attachKeyboardListeners()
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, showTitle = true, title = title.toString())
            createAnalysisBtn.setOnClickListener {
                val validationResult = gasAirAnalysisDataFields.validate()
                if (validationResult.isValid) {
                    proceedCreation(gasAirAnalysisDataFields.gatherData())
                }
            }
        }
    }

    private fun setupSteps(workPermit: WorkPermitDetailsUi) {
        with(binding.gasAirAnalysisDataFields) {
            setOnFormFilledListener {
                binding.createAnalysisBtn.isEnabled = it
            }
            addValidators(
                DateValidator(
                    TAG_PROBE_DATE_TIME,
                ),
                DateValidator(
                    TAG_PROBE_NEXT_DATE,
                    type = PatternDataType.DATE,
                    managerClass = DateManager::class.java
                ),
                DateBetweenTwoDatesValidator(
                    TAG_PROBE_DATE_TIME,
                    firstDate = workPermit.mainInfo.startDate.toLocalDateTime(workPermit.mainInfo.startTime),
                    secondDate = workPermit.mainInfo.endDate.toLocalDateTime(workPermit.mainInfo.endTime),
                )
            )
            inflateOf(fields, true)
        }
    }

    private fun proceedCreation(data: MutableMap<String, Any?>) {
        val deviceModelAndNumber = data[TAG_PROBE_DEVICE] as Pair<String, String>
        val gasAirAnalysisData = CreateGasAirAnalysisData(
            workPermitId = workPermitId,
            date = data[TAG_PROBE_DATE_TIME] as String,
            place = data[TAG_PROBE_PLACE] as String,
            result = data[TAG_PROBE_CONCENTRATION] as String,
            components = data[TAG_PROBE_COMPONENTS] as String,
            concentration = data[TAG_PROBE_PERMISSIBLE_CONCENTRATION] as String,
            dateNext = data[TAG_PROBE_NEXT_DATE] as String,
            deviceModel = deviceModelAndNumber.first,
            deviceNumber = deviceModelAndNumber.second,
        )
        viewModel.addAirAnalysis(gasAirAnalysisData)
    }

    override fun onShowKeyboard() {
        binding.createAnalysisBtn.isGone = true
    }

    override fun onHideKeyboard() {
        binding.createAnalysisBtn.isVisible = true
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {
        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        private const val TAG_PROBE_DATE_TIME = "date_time"
        private const val TAG_PROBE_PLACE = "place"
        private const val TAG_PROBE_COMPONENTS = "components"
        private const val TAG_PROBE_PERMISSIBLE_CONCENTRATION = "permissible_concentration"
        private const val TAG_PROBE_CONCENTRATION = "concentration"
        private const val TAG_PROBE_DEVICE = "device"
        private const val TAG_PROBE_NEXT_DATE = "next_date"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, CreateGasAirAnalysisActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}

class CreateGasAirAnalysisData(
    val workPermitId: Long,
    val date: String,
    val place: String,
    val result: String,
    val components: String,
    val concentration: String,
    val dateNext: String,
    val deviceModel: String,
    val deviceNumber: String,
)
