package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityGasAirAnalyzesBinding
import ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler.GasAirAnalysisAdapter
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.GasAirAnalysisUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx

@AndroidEntryPoint
class GasAirAnalyzesActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val viewModel: GasAirAnalyzesViewModel by viewModels()
    private val binding: ActivityGasAirAnalyzesBinding by viewBinding(CreateMethod.INFLATE)

    private val gasAirAnalysisAdapter by lazy {
        GasAirAnalysisAdapter(::onDeleteGasAirAnalysis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.initGasAirAnalyzes(workPermitId)
    }

    private fun setupObservers() {
        viewModel.workPermit.observe(this, ::setupWorkPermit)
        viewModel.adapterItems.observe(this) {
            gasAirAnalysisAdapter.items = it
        }
        viewModel.isInOfflineMode.observe(this) { inOffline ->
            gasAirAnalysisAdapter.setDeleteAvailable(inOffline.not())
        }
    }

    private fun setupWorkPermit(workPermit: WorkPermitDetailsUi) {
        (workPermit.additionalInfo as? SignedAdditionalInfo)?.let {
            if (workPermit.isCreator) {
                binding.addGasAirAnalysisBtn.isVisible = true
                binding.addGasAirAnalysisBtn.setOnClickListener {
                    viewModel.openCreateGasAirAnalysisScreen(workPermitId)
                }
            } else {
                binding.addGasAirAnalysisBtn.isGone = true
            }
        }
            ?: throw IllegalStateException("GasAirAnalyzesActivity should not be called, when work permit is not in SignedState")
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            setToolbar(toolbarContainer.toolbar, title = title.toString(), showTitle = true)
            with(gasAirAnalyzesRecycler) {
                adapter = gasAirAnalysisAdapter
                layoutManager = LinearLayoutManager(this@GasAirAnalyzesActivity)
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

    private fun onDeleteGasAirAnalysis(item: GasAirAnalysisUi) {
        ConfirmationDialogBuilder(this)
            .setOnPositiveButtonAction {
                viewModel.deleteGasAirAnalysis(item)
            }
            .setTitle(R.string.dialog_title_delete_gas_air_analysis)
            .setPositiveButtonText(R.string.dialog_btn_delete_positive)
            .setNegativeButtonText(R.string.dialog_btn_negative)
            .show()
    }

    companion object {

        private const val SPACE_BETWEEN_ITEMS = 16

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, GasAirAnalyzesActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
