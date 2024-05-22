package ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentWorkTypeBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepCallback
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler.NumberedItemUi
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler.WorkTypeAdapter

@AndroidEntryPoint
class WorkTypeFragment : Fragment(R.layout.fragment_work_type) {

    private val viewModel: WorkTypeViewModel by viewModels()
    private val binding: FragmentWorkTypeBinding by viewBinding()
    private lateinit var stepCallback: StepCallback
    private lateinit var workTypeCallback: WorkTypeCallback

    private val workTypeAdapter by lazy {
        WorkTypeAdapter(this::onWorkTypeClicked)
    }

    private fun onWorkTypeClicked(item: NumberedItemUi) {
        workTypeCallback.setWorkTypeName(item.title)
        stepCallback.endStep(WorkTypeStepOutput(item.itemId), null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        stepCallback = requireListener()
        workTypeCallback = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()

        viewModel.loadWorkTypes()
    }

    private fun setupObservers() {
        viewModel.workTypes.observe(viewLifecycleOwner) {
            workTypeAdapter.items = it
        }
    }

    private fun setupUi() {
        with(binding) {
            workTypesRecycler.adapter = workTypeAdapter
            workTypesRecycler.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
