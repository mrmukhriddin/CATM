package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.core.ui.utils.setupSearch
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.DialogPickEmployeeBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeesAdapter
import ru.metasharks.catm.utils.argumentDelegate
import timber.log.Timber

@AndroidEntryPoint
class PickEmployeeDialog : BottomSheetDialogFragment() {

    private val binding: DialogPickEmployeeBinding by viewBinding(CreateMethod.INFLATE)
    private val initialPickedEmployees: List<EmployeeUi> by argumentDelegate(ARG_PICKED_EMPLOYEES)

    private lateinit var callback: Callback

    private val viewModel: PickEmployeeViewModel by viewModels()

    private val pickItemAdapter: EmployeesAdapter by lazy {
        EmployeesAdapter(
            this::onAddButtonClicked,
            this::onNearEndListener
        )
    }

    private fun onAddButtonClicked(workerUi: EmployeeUi) {
        viewModel.selectItem(workerUi)
        callback.onItemPick(workerUi, workerUi.isSelected)
    }

    private fun onNearEndListener() {
        viewModel.load(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = requireListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            ru.metasharks.catm.core.ui.R.style.KeyboardBottomSheetDialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        setupUi()
        setupObservers()
        viewModel.setInitialItems(initialPickedEmployees)
        viewModel.load(false)
    }

    private fun setupObservers() {
        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            pickItemAdapter.notifyItemChanged(item)
        }
        viewModel.adapterItems.observe(viewLifecycleOwner) { items ->
            pickItemAdapter.items = items
        }
    }

    private fun setupUi() {
        binding.itemsRecycler.apply {
            adapter = pickItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    interface Callback {

        fun onItemPick(item: EmployeeUi, isSelected: Boolean)
    }

    private fun setupSearch() {
        binding.searchField
            .setupSearch()
            .subscribe(
                { message ->
                    viewModel.search(message)
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    companion object {

        fun newInstance(pickedEmployees: List<EmployeeUi>): PickEmployeeDialog {
            val fragment = PickEmployeeDialog()
            fragment.arguments = bundleOf(
                ARG_PICKED_EMPLOYEES to pickedEmployees,
            )
            return fragment
        }

        private const val ARG_PICKED_EMPLOYEES = "picked_employees"

        const val TAG = "PickEmployeeDialog"
    }
}
