package ru.metasharks.catm.feature.workpermit.ui.filter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.core.util.Pair
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.utils.setCustomChecked
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitsFilterBinding
import ru.metasharks.catm.feature.workpermit.ui.filter.dialog.choose.ResponsibleManagersViewModel
import ru.metasharks.catm.feature.workpermit.ui.filter.dialog.choose.WorkTypeViewModel
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.date.LocalDateUtils

@AndroidEntryPoint
internal class WorkPermitsFilterActivity : BaseActivity(), PickItemDialog.Callback {

    private val binding: ActivityWorkPermitsFilterBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WorkPermitsFilterViewModel by viewModels()

    private val filterInput: FilterData? by argumentDelegate(GetFilters.EXTRA_FILTER_INPUT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFilters()
        setupObservers()
        setupToolbar()
        setupUi()
    }

    private fun setupObservers() {
        viewModel.resultIntent.observe(this) { (code, intent) ->
            if (intent == null) {
                setResult(RESULT_CANCELED)
                return@observe
            }
            setResult(code, intent)
        }
    }

    private fun setupUi() {
        binding.dateField.setOnClickListener {
            val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
            viewModel.filterOutput?.let {
                val createdAfter = it.createdAfter?.toDateTime(LocalTime())?.millis ?: return@let
                val createdBefore = it.createdBefore?.toDateTime(LocalTime())?.millis ?: return@let
                datePickerBuilder.setSelection(Pair(createdAfter, createdBefore))
            }

            val picker = datePickerBuilder.build()
            picker.addOnPositiveButtonClickListener { timestamps ->
                if (timestamps.first == null || timestamps.second == null) {
                    return@addOnPositiveButtonClickListener
                }
                val firstDate = LocalDate(timestamps.first)
                val secondDate = LocalDate(timestamps.second)
                setDateText(firstDate, secondDate)
                setDateResultIntent(firstDate, secondDate)
            }
            picker.show(supportFragmentManager, picker.toString())
        }
        binding.workTypeField.setOnClickListener {
            val workTypeId = viewModel.filterOutput?.workTypeId
            val workTypeName = viewModel.filterOutput?.workTypeName
            val workType = if (workTypeId != null && workTypeName != null) {
                PickItemDialog.ItemUi(entityId = workTypeId, value = workTypeName)
            } else {
                null
            }
            PickItemDialog.Builder(this)
                .setTitle(R.string.work_type)
                .setInitialItem(workType)
                .setViewModelClass(WorkTypeViewModel::class.java)
                .build()
                .show(supportFragmentManager, TAG_WORK_TYPE)
        }
        binding.supervisorField.setOnClickListener {
            val managerId = viewModel.filterOutput?.responsibleManagerId
            val managerName = viewModel.filterOutput?.responsibleManagerName
            val manager = if (managerId != null && managerName != null) {
                PickItemDialog.ItemUi(entityId = managerId, value = managerName)
            } else {
                null
            }
            PickItemDialog.Builder(this)
                .setTitle(R.string.supervisor)
                .setInitialItem(manager)
                .setViewModelClass(ResponsibleManagersViewModel::class.java)
                .build()
                .show(supportFragmentManager, TAG_RESPONSIBLE_MANAGER_ID)
        }
        binding.needSignField.setOnCheckedChangeListener(this::onCheck)
        binding.saveFiltersBtn.setOnClickListener {
            viewModel.saveResultAndExit()
        }
        binding.resetFiltersBtn.setOnClickListener {
            resetFilters()
        }
    }

    private fun onCheck(compoundButton: CompoundButton, isChecked: Boolean) {
        viewModel.setNeedSign(isChecked)
    }

    private fun setFilters() {
        viewModel.setInitFilter(filterInput)
        if (filterInput?.createdAfter != null && filterInput?.createdBefore != null) {
            setDateText(
                requireNotNull(filterInput?.createdAfter),
                requireNotNull(filterInput?.createdBefore)
            )
        }
        if (filterInput?.workTypeId != null) {
            binding.workTypeField.text = filterInput?.workTypeName
        }
        if (filterInput?.responsibleManagerId != null) {
            binding.supervisorField.text = filterInput?.responsibleManagerName
        }
        if (filterInput?.needSign == true) {
            binding.needSignField.setCustomChecked(true, this::onCheck)
        }
    }

    private fun resetFilters() {
        viewModel.reset()
        binding.dateField.text = ""
        binding.workTypeField.text = ""
        binding.supervisorField.text = ""
        binding.needSignField.setCustomChecked(false, this::onCheck)
    }

    private fun setDateText(dateAfter: LocalDate, dateBefore: LocalDate) {
        val dateAfterPatterned = dateAfter.toString(LocalDateUtils.DAY_OF_YEAR_PATTERN)
        val dateBeforePatterned = dateBefore.toString(LocalDateUtils.DAY_OF_YEAR_PATTERN)
        binding.dateField.text =
            if (dateAfterPatterned != dateBeforePatterned) {
                getString(R.string.wp_dates_pattern, dateAfterPatterned, dateBeforePatterned)
            } else {
                getString(R.string.wp_single_date_pattern, dateBeforePatterned)
            }
    }

    private fun setDateResultIntent(first: LocalDate, second: LocalDate) {
        viewModel.setDate(first, second)
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showNavigate = true,
            showTitle = true,
            title = title.toString()
        )
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    override fun onItemPicked(tag: String, item: PickItemDialog.ItemUi?) {
        when (tag) {
            TAG_WORK_TYPE -> {
                if (item == null) {
                    binding.workTypeField.text = ""
                    viewModel.setWorkType(null, null)
                } else {
                    binding.workTypeField.text = item.value
                    viewModel.setWorkType(item.entityId, item.value)
                }
            }
            TAG_RESPONSIBLE_MANAGER_ID -> {
                if (item == null) {
                    binding.supervisorField.text = ""
                    viewModel.setResponsibleManager(null, null)
                } else {
                    binding.supervisorField.text = item.value
                    viewModel.setResponsibleManager(item.entityId, item.value)
                }
            }
        }
    }

    companion object {

        const val TAG_WORK_TYPE = "work_type"
        const val TAG_RESPONSIBLE_MANAGER_ID = "responsible_manager_id"
    }
}

class GetFilters : ActivityResultContract<FilterData?, GetFilters.Payload>() {

    override fun createIntent(context: Context, input: FilterData?): Intent {
        return Intent(context, WorkPermitsFilterActivity::class.java)
            .putExtra(EXTRA_FILTER_INPUT, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Payload {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            Payload(
                filterData = intent.getParcelableExtra(EXTRA_FILTER_OUTPUT),
                code = CODE_SET,
            )
        } else {
            Payload(
                filterData = null,
                code = CODE_BACK,
            )
        }
    }

    data class Payload(
        val filterData: FilterData?,
        val code: Int,
    )

    companion object {

        const val EXTRA_FILTER_OUTPUT = "filter_output"
        const val EXTRA_FILTER_INPUT = "filter_input"

        const val CODE_BACK = 0
        const val CODE_SET = 1
    }
}
