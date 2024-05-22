package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityDailyPermitsBinding
import ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.recycler.DailyPermitsAdapter
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx

@AndroidEntryPoint
class DailyPermitsActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val binding: ActivityDailyPermitsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: DailyPermitsViewModel by viewModels()

    private val dailyPermitsAdapter by lazy {
        DailyPermitsAdapter(
            ::onDeleteClicked,
            ::onEndAndSignClicked,
            ::onSignClicked,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.init(workPermitId, forceRefresh = false)
    }

    private fun onEndAndSignClicked(item: DailyPermitUi) {
        viewModel.openEndDailyPermitScreen(item)
    }

    private fun onDeleteClicked(item: DailyPermitUi) {
        showDeletionDialog(item)
    }

    private fun onSignClicked(item: DailyPermitUi) {
        viewModel.signDailyPermit(item)
    }

    private fun showDeletionDialog(item: DailyPermitUi) {
        ConfirmationDialogBuilder(this)
            .setTitle(R.string.dialog_title_delete_daily_permit)
            .setPositiveButtonText(R.string.btn_delete_text)
            .setNegativeButtonText(R.string.dialog_btn_negative)
            .setOnPositiveButtonAction {
                viewModel.deleteDailyPermit(item)
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.payload.observe(this) { payload ->
            when (payload) {
                is DailyPermitsViewModel.Payload.DailyPermits -> {
                    dailyPermitsAdapter.items = emptyList()
                    dailyPermitsAdapter.items = payload.dailyPermitsItem
                    if (payload.isCreator) {
                        binding.addDailyPermitBtn.isVisible = true
                        binding.addDailyPermitBtn.setOnClickListener {
                            viewModel.openCreateDailyPermitScreen(workPermitId)
                        }
                    }
                }
            }
        }
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, showTitle = true, title = title.toString())
            with(dailyPermitsRecycler) {
                layoutManager = LinearLayoutManager(this@DailyPermitsActivity)
                adapter = dailyPermitsAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        dpToPx(SPACE_BETWEEN_ITEMS),
                        orientation = DividerItemDecoration.VERTICAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                    )
                )
            }
        }
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val SPACE_BETWEEN_ITEMS = 16

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, DailyPermitsActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
