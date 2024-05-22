package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.end

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityEndDailyPermitBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.validator.DateAfterDayValidator
import ru.metasharks.catm.step.validator.DateValidator
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

@AndroidEntryPoint
internal class EndDailyPermitActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)
    private val dailyPermitId: Long by argumentDelegate(EXTRA_DAILY_PERMIT_ID)

    private val binding: ActivityEndDailyPermitBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: EndDailyPermitViewModel by viewModels()

    private var fields: List<Field<out Any>> = emptyList()

    @Inject
    lateinit var errorHandler: ErrorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId, forceRefresh = false)
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            binding.endDailyPermitBtn.isLoading = false
            errorHandler.handle(binding.endDailyPermitBtn, error)
        }
        viewModel.workPermit.observe(this) { workPermit ->
            val additionalInfo = workPermit.additionalInfo as SignedAdditionalInfo
            val dailyPermit =
                additionalInfo.dailyPermitsList.first { it.dailyPermitId == dailyPermitId }
            fields = listOf(
                Field.DateAndTime(
                    tag = TAG_WORK_END,
                    input = Field.DateAndTime.Input(
                        StringResWrapper(R.string.field_label_dp_work_end),
                        initialValue = dailyPermit.dateStart to "",
                        canEditDate = false
                    )
                ),
            )
            binding.endDailyPermitBtn.setOnClickListener { proceedEndDailyPermit(dailyPermit) }
            setSteps(dailyPermit)
        }
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, showTitle = true, title = title.toString())
        }
    }

    private fun proceedEndDailyPermit(dailyPermit: DailyPermitUi) {
        val validationResult = binding.endDailyPermitSteps.validate()
        if (validationResult.isValid) {
            val dateEnd = binding.endDailyPermitSteps.gatherDataFromField(TAG_WORK_END) as String
            binding.endDailyPermitBtn.isLoading = true
            viewModel.endDailyPermit(dailyPermit, dateEnd)
        }
    }

    private fun setSteps(dailyPermit: DailyPermitUi) {
        with(binding.endDailyPermitSteps) {
            inflateOf(fields, setNecessaryItems = true)
            setOnFormFilledListener(binding.endDailyPermitBtn::setEnabled)
            addValidators(
                DateValidator(
                    TAG_WORK_END,
                    warning = StringResWrapper(ru.metasharks.catm.feature.step.R.string.warning_date_wrong_format)
                ),
                DateAfterDayValidator(
                    TAG_WORK_END,
                    dateToCompare = LocalDateUtils.parseLocalDateTime("${dailyPermit.dateStart} ${dailyPermit.timeStart}"),
                    warning = StringResWrapper(R.string.warning_start_date_must_be_after_end_date)
                )
            )
        }
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"
        private const val EXTRA_DAILY_PERMIT_ID = "daily_permit_id"

        private const val TAG_WORK_END = "work_end"

        fun createIntent(context: Context, workPermitId: Long, dailyPermitId: Long): Intent {
            return Intent(context, EndDailyPermitActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
                .putExtra(EXTRA_DAILY_PERMIT_ID, dailyPermitId)
        }
    }
}
