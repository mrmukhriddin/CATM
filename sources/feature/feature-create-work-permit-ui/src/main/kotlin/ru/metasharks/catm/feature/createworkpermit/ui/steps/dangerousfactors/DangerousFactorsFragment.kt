package ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentStepPatternBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepFragment
import ru.metasharks.catm.step.StepPatternLayout
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.step.entities.StepPatternRestoreData
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.utils.argumentDelegate

@AndroidEntryPoint
class DangerousFactorsFragment : StepFragment(R.layout.fragment_step_pattern) {

    private val binding: FragmentStepPatternBinding by viewBinding()
    private val viewModel: DangerousFactorsViewModel by viewModels()

    private val restoreData: StepPatternRestoreData? by argumentDelegate(ARG_RESTORE_DATA)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        viewModel.load(restoreData)
    }

    private fun setupObservers() {
        viewModel.createData.observe(viewLifecycleOwner) {
            setupSteps(it)
        }
    }

    private fun setupSteps(createData: List<StepPatternLayout.CreateItemData>) {
        with(binding.stepPatterns) {
            setType(StatusListener.AT_LEAST_ONE)
            inflateItems(createData, true)
        }
    }

    private fun setupUi() {
        binding.nextBtn.isEnabled = false
        binding.nextBtn.setOnClickListener {
            stepCallback.endStep(
                dataToOutput(),
                StepPatternRestoreData(binding.stepPatterns.gatherRestoreData())
            )
        }
        binding.backBtn.setOnClickListener {
            stepCallback.back()
        }
        binding.stepPatterns.setOnFormFilledListener {
            binding.nextBtn.isEnabled = it
        }
    }

    private fun dataToOutput(): DangerousFactorsStepOutput {
        val dangerous =
            binding.stepPatterns.gatherNonNullData<Long>(DangerousFactorsViewModel.PREFIX_DANGEROUS)
        val another =
            binding.stepPatterns.gatherNonNullData<Long>(DangerousFactorsViewModel.PREFIX_ANOTHER)
        val save =
            binding.stepPatterns.gatherNonNullData<Long>(DangerousFactorsViewModel.PREFIX_SAVE)
        return DangerousFactorsStepOutput(
            dangerous.values.toList(), another.values.toList(), save.values.toList()
        )
    }

    companion object {

        private const val ARG_RESTORE_DATA = "restore_data"

        fun newInstance(restoreData: RestoreData?): Fragment {
            val fragment = DangerousFactorsFragment()
            fragment.arguments = bundleOf(
                ARG_RESTORE_DATA to restoreData
            )
            return fragment
        }
    }
}
