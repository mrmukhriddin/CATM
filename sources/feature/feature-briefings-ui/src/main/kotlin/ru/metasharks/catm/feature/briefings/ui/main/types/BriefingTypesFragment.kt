package ru.metasharks.catm.feature.briefings.ui.main.types

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.databinding.FragmentBriefingTypesBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainCallbacks
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainViewModel
import ru.metasharks.catm.utils.argumentDelegate

@AndroidEntryPoint
class BriefingTypesFragment : Fragment(R.layout.fragment_briefing_types) {

    private val viewModel: BriefingTypesViewModel by viewModels()
    private val binding: FragmentBriefingTypesBinding by viewBinding()
    private val mainViewModel: BriefingMainViewModel
        get() = hostCallbacks.getMainViewModel()

    private lateinit var hostCallbacks: BriefingMainCallbacks
    private val categoryId: Int by argumentDelegate(ARG_CATEGORY_ID)
    private val adapter = BriefingTypesAdapter(::onTypeClick)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostCallbacks = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialData(categoryId)
        setupObservables()
        setupUi()
    }

    private fun setupUi() {
        binding.briefingTypesRecycler.adapter = adapter
        binding.briefingTypesRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservables() {
        // main view model
        mainViewModel.briefingCategories.observe(viewLifecycleOwner) { categories ->
            val category = categories.first { it.categoryId == categoryId }
            binding.header.text = category.text
        }
        mainViewModel.briefings.observe(viewLifecycleOwner) { briefings ->
            viewModel.loadTypes(briefings = briefings)
        }
        mainViewModel.briefingTypes.observe(viewLifecycleOwner) { types ->
            viewModel.loadTypes(types = types)
        }
        mainViewModel.dataLoading.observe(viewLifecycleOwner) { dataLoading ->
            binding.briefingTypesContainer.isRefreshing = dataLoading
        }
        mainViewModel.isInOfflineModeLiveData.observe(viewLifecycleOwner) { isInOffline ->
            with(binding) {
                if (isInOffline) {
                    briefingTypesContainer.isEnabled = false
                } else {
                    briefingTypesContainer.setOnRefreshListener {
                        mainViewModel.loadOnlineData()
                    }
                }
            }
        }
        // current view model
        viewModel.filteredBriefingTypes.observe(viewLifecycleOwner) { filteredTypes ->
            adapter.setTypes(filteredTypes)
        }
    }

    private fun onTypeClick(type: BriefingTypeUi) {
        hostCallbacks.openBriefingsList(categoryId, type.typeId)
    }

    companion object {

        private const val ARG_CATEGORY_ID: String = "category_id"

        fun newInstance(categoryId: Int): BriefingTypesFragment =
            BriefingTypesFragment().apply {
                arguments = bundleOf(
                    ARG_CATEGORY_ID to categoryId
                )
            }
    }
}
