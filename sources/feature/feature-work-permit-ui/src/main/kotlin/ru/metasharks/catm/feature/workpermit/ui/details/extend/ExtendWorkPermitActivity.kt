package ru.metasharks.catm.feature.workpermit.ui.details.extend

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
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityExtendWorkPermitBinding
import ru.metasharks.catm.feature.workpermit.ui.details.dialogs.ResponsibleEmployeesViewModel
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.validator.DateValidator
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

@AndroidEntryPoint
class ExtendWorkPermitActivity : BaseActivity(), PickItemDialog.Callback {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val binding: ActivityExtendWorkPermitBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: ExtendWorkPermitViewModel by viewModels()

    private val fields = listOf(
        Field.DateAndTime(
            tag = TAG_EXTEND_UNTIL,
            input = Field.DateAndTime.Input(
                label = StringResWrapper(R.string.field_label_extend_until),
            )
        ),
        Field.Pick(
            tag = TAG_PERMIT_ISSUER,
            input = Field.Pick.Input(
                label = StringResWrapper(R.string.field_label_extend_issuer),
                hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_pick),
                onSelectCallback = ::onSelectIssuer,
            )
        ),
    )

    private fun onSelectIssuer(view: View) {
        PickItemDialog.Builder(this)
            .setTitle(getString(R.string.field_label_extend_issuer))
            .setInitialItem(view.tag as? PickItemDialog.ItemUi)
            .setViewModelClass(ResponsibleEmployeesViewModel::class.java)
            .build()
            .show(supportFragmentManager, TAG_PERMIT_ISSUER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId, forceRefresh = false)
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            errorHandler.handle(binding.extendWarningBtn, error)
        }
        viewModel.workPermit.observe(this) { workPermit ->
            val mainInfo = workPermit.mainInfo
            with(binding) {
                wpWorkStart.text = getHighlightedString(
                    this@ExtendWorkPermitActivity,
                    R.string.dp_work_permit_start,
                    LocalDateUtils.toString(mainInfo.startDate, mainInfo.startTime)
                )
                wpWorkEnd.text = getHighlightedString(
                    this@ExtendWorkPermitActivity,
                    R.string.dp_work_permit_end,
                    LocalDateUtils.toString(mainInfo.endDate, mainInfo.endTime)
                )
            }
        }
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, showTitle = true, title = title.toString())
            extendDataSteps.inflateOf(fields, false)
            extendDataSteps.setNecessaryTags(
                setOf(
                    TAG_EXTEND_UNTIL,
                    TAG_PERMIT_ISSUER,
                )
            )
            extendDataSteps.setOnFormFilledListener {
                extendWarningBtn.isEnabled = it
            }
            extendDataSteps.addValidators(
                DateValidator(
                    TAG_EXTEND_UNTIL,
                    warning = StringResWrapper(ru.metasharks.catm.feature.step.R.string.warning_date_wrong_format),
                )
            )
            extendWarningBtn.setOnClickListener {
                if (extendDataSteps.validate().isValid) {
                    processExtension()
                }
            }
        }
    }

    private fun processExtension() {
        val data = binding.extendDataSteps.gatherData()
        viewModel.extend(
            workPermitId,
            ExtendWorkPermitData(
                permitIssuerId = data[TAG_PERMIT_ISSUER] as Long,
                date = data[TAG_EXTEND_UNTIL] as String,
            )
        )
    }

    override fun onItemPicked(tag: String, item: PickItemDialog.ItemUi?) {
        binding.extendDataSteps.setValueForFieldWithTag(tag, item)
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        private const val TAG_EXTEND_UNTIL = "extend_until"
        private const val TAG_PERMIT_ISSUER = "permit"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, ExtendWorkPermitActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
