package ru.metasharks.catm.feature.workpermit.ui.details.riskfactors

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityRiskFactorsBinding
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemStageBinding
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.recycler.RiskFactorsAdapter
import ru.metasharks.catm.utils.dpToPx

@AndroidEntryPoint
class RiskFactorsActivity : BaseActivity() {

    private val binding: ActivityRiskFactorsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: RiskFactorsViewModel by viewModels()

    private val riskFactorsAdapter: RiskFactorsAdapter by lazy {
        RiskFactorsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupUi()
        setupObservers()

        viewModel.init()
    }

    private fun setupObservers() {
        viewModel.stages.observe(this) { stages ->
            val firstStage = stages.first()
            with(binding) {
                stageName.text = firstStage.stageName
                with(stagesContainer) {
                    stages.forEachIndexed { index, stage ->
                        addView(createStageItem(stage, index == 0))
                    }
                }
            }
            riskFactorsAdapter.items = firstStage.riskFactors
        }
    }

    private fun LinearLayout.createStageItem(stage: RiskFactorStageUi, isSelected: Boolean): View {
        val binding = ItemStageBinding.inflate(layoutInflater, this, false)
        with(binding.root) {
            tag = stage.stageId
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            text = stage.stageIdName
            setSelected(isSelected)
            setOnClickListener { onStageSelected(it, stage) }
        }
        return binding.root
    }

    private fun onStageSelected(view: View, stage: RiskFactorStageUi) {
        with(binding) {
            stagesContainer.children.firstOrNull { it.isSelected }?.let {
                it.isSelected = false
            }
            view.isSelected = true
            riskFactorsAdapter.items = stage.riskFactors
            stageName.text = stage.stageName
        }
    }

    private fun setupToolbar() {
        setToolbar(binding.toolbarContainer.toolbar, showTitle = true, title = title.toString())
    }

    private fun setupUi() {
        with(binding) {
            with(stageRiskFactorsRecycler) {
                adapter = riskFactorsAdapter
                layoutManager = LinearLayoutManager(this@RiskFactorsActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        dpToPx(RISK_FACTORS_SPACE),
                        orientation = DividerItemDecoration.VERTICAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END,
                    )
                )
            }
        }
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val RISK_FACTORS_SPACE = 16

        fun createIntent(context: Context): Intent {
            return Intent(context, RiskFactorsActivity::class.java)
        }
    }
}
