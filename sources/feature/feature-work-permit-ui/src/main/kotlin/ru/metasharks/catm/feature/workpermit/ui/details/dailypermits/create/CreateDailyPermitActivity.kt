package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityCreateDailyPermitBinding
import ru.metasharks.catm.feature.workpermit.ui.details.dialogs.ResponsibleEmployeesViewModel
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.validator.DateValidator
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

@AndroidEntryPoint
class CreateDailyPermitActivity : BaseActivity(), PickItemDialog.Callback {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val binding: ActivityCreateDailyPermitBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: CreateDailyPermitViewModel by viewModels()

    private val fields = listOf(
        Field.DateAndTime(
            tag = TAG_WORK_START,
            input = Field.DateAndTime.Input(
                StringResWrapper(R.string.field_label_dp_work_start),
            )
        ),
        Field.Pick(
            tag = TAG_PERMITTER_ID,
            input = Field.Pick.Input(
                StringResWrapper(R.string.field_label_dp_permitter),
                hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = ::onPickPermitter
            )
        ),
    )

    private fun onPickPermitter(view: View) {
        PickItemDialog.Builder(this)
            .setTitle(getString(R.string.field_label_dp_permitter))
            .setInitialItem(view.tag as? PickItemDialog.ItemUi)
            .setViewModelClass(ResponsibleEmployeesViewModel::class.java)
            .build()
            .show(supportFragmentManager, TAG_PERMITTER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId, forceRefresh = false)
    }

    private fun setupObservers() {
        viewModel.workPermit.observe(this) { workPermit ->
            val mainInfo = workPermit.mainInfo
            with(binding) {
                dpWorkStart.text = getHighlightedString(
                    this@CreateDailyPermitActivity,
                    R.string.dp_work_permit_start,
                    LocalDateUtils.toString(mainInfo.startDate, mainInfo.startTime)
                )
                dpWorkEnd.text = getHighlightedString(
                    this@CreateDailyPermitActivity,
                    R.string.dp_work_permit_end,
                    LocalDateUtils.toString(mainInfo.endDate, mainInfo.endTime)
                )
            }
        }
        viewModel.error.observe(this) { error ->
            binding.createDailyPermitBtn.isLoading = false
            errorHandler.handle(binding.createDailyPermitBtn, error)
        }
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, showTitle = true, title = title.toString())
            setSteps()
            createDailyPermitBtn.setOnClickListener {
                val validationResult = binding.createDailyPermitSteps.validate()
                if (validationResult.isValid) {
                    proceedCreation(createDailyPermitSteps.gatherData())
                    createDailyPermitBtn.isLoading = true
                }
            }
        }
    }

    private fun proceedCreation(data: MutableMap<String, Any?>) {
        val creationData = CreateDailyPermitData(
            workPermitId = workPermitId,
            permitterId = data[TAG_PERMITTER_ID] as Long,
            permitterName = (binding.createDailyPermitSteps.gatherRestoreDataFromField(
                TAG_PERMITTER_ID
            ) as Field.Pick.InitialValue).item.value,
            date = data[TAG_WORK_START] as String,
        )
        viewModel.createDailyPermit(creationData)
    }

    private fun setSteps() {
        with(binding.createDailyPermitSteps) {
            inflateOf(fields, setNecessaryItems = true)
            addValidators(
                DateValidator(
                    TAG_WORK_START,
                    warning = StringResWrapper(ru.metasharks.catm.feature.step.R.string.warning_date_wrong_format)
                )
            )
            setOnFormFilledListener(binding.createDailyPermitBtn::setEnabled)
        }
    }

    override fun onItemPicked(tag: String, item: PickItemDialog.ItemUi?) {
        binding.createDailyPermitSteps.setValueForFieldWithTag(tag, item)
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        private const val TAG_WORK_START = "work_start"
        private const val TAG_PERMITTER_ID = "permitter_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, CreateDailyPermitActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}

class CreateDailyPermitData(

    val workPermitId: Long,

    val permitterId: Long,

    val date: String,

    val permitterName: String,
)
