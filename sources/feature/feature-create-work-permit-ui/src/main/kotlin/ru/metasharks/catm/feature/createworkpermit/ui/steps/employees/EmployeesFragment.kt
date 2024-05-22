package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentWorkersBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepFragment
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.dialog.PickEmployeeDialog
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeesAdapter
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.utils.argumentDelegate

@AndroidEntryPoint
class EmployeesFragment : StepFragment(R.layout.fragment_workers), PickEmployeeDialog.Callback {

    private val binding: FragmentWorkersBinding by viewBinding()
    private val restoreData: EmployeesRestoreData? by argumentDelegate(ARG_RESTORE_DATA)

    private val workersAdapter by lazy {
        EmployeesAdapter(this::onWorkerRemoveClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        restoreUi()
    }

    private fun restoreUi() {
        restoreData?.let {
            workersAdapter.items = EmployeesRestoreData.restoreToWorkerItems(it.workerItems)
        }
        binding.saveBtn.isEnabled = workersAdapter.items.isNullOrEmpty().not()
    }

    private fun setupUi() {
        with(binding) {
            addWorkersBtn.setOnClickListener {
                PickEmployeeDialog.newInstance(workersAdapter.getSelectedWorkers())
                    .show(childFragmentManager, PickEmployeeDialog.TAG)
            }
            workersRecycler.apply {
                adapter = workersAdapter
                layoutManager = LinearLayoutManager(requireContext())
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            saveBtn.setOnClickListener {
                stepCallback.endStep(
                    EmployeesStepOutput(
                        workersAdapter.items?.filter { it is EmployeeUi && it.isSelected }
                            ?.map { (it as EmployeeUi).workerId }.orEmpty()
                    ),
                    gatherRestoreData()
                )
            }
            saveBtn.isEnabled = workersAdapter.items.isNullOrEmpty().not()
            backBtn.setOnClickListener {
                stepCallback.back()
            }
        }
    }

    private fun gatherRestoreData(): EmployeesRestoreData {
        val workerItems = workersAdapter.getWorkers()
        return EmployeesRestoreData(EmployeesRestoreData.workerItemsToRestore(workerItems))
    }

    override fun onItemPick(item: EmployeeUi, isSelected: Boolean) {
        if (isSelected) {
            workersAdapter.items = workersAdapter.items.orEmpty().plus(item)
        } else {
            workersAdapter.items = workersAdapter.items.orEmpty().filter { it.id != item.id }
        }
        refreshSaveBtnState()
    }

    private fun onWorkerRemoveClicked(workerUi: EmployeeUi) {
        workersAdapter.removeItem(workerUi)
        refreshSaveBtnState()
    }

    private fun refreshSaveBtnState() {
        binding.saveBtn.isEnabled = workersAdapter.items.isNullOrEmpty().not()
    }

    companion object {

        private const val ARG_RESTORE_DATA = "restore_data"

        fun newInstance(restoreData: RestoreData?): Fragment {
            val fragment = EmployeesFragment()
            fragment.arguments = bundleOf(
                ARG_RESTORE_DATA to restoreData
            )
            return fragment
        }
    }
}
