package ru.metasharks.catm.feature.briefings.ui.main.briefings

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
import ru.metasharks.catm.feature.briefings.ui.databinding.FragmentBriefingsListBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainCallbacks
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainViewModel

@AndroidEntryPoint
class BriefingsListFragment : Fragment(R.layout.fragment_briefings_list) {

    private val viewModel: BriefingsListViewModel by viewModels()
    private val binding: FragmentBriefingsListBinding by viewBinding()
    private val mainViewModel: BriefingMainViewModel
        get() = hostCallbacks.getMainViewModel()

    private lateinit var hostCallbacks: BriefingMainCallbacks
    private val categoryId: Int by lazy {
        requireArguments().getInt(ARG_CATEGORY_ID)
    }
    private val typeId: Int by lazy {
        requireArguments().getInt(ARG_TYPE_ID)
    }
    private val adapter = BriefingItemsAdapter(::onBriefingClick)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostCallbacks = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialData(categoryId, typeId)
        setupObservables()
        setupUi()
    }

    private fun setupUi() {
        binding.briefingsListRecycler.adapter = adapter
        binding.briefingsListRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservables() {
        // main view model
        mainViewModel.briefingCategories.observe(viewLifecycleOwner) { categories ->
            val category = categories.first { it.categoryId == categoryId }
            binding.briefingsListHeader.text = category.text
        }
        mainViewModel.briefings.observe(viewLifecycleOwner) { briefings ->
            viewModel.filterBriefings(briefings = briefings)
        }
        mainViewModel.dataLoading.observe(viewLifecycleOwner) { dataLoading ->
            binding.briefingsListContainer.isRefreshing = dataLoading
        }
        mainViewModel.isInOfflineModeLiveData.observe(viewLifecycleOwner) { isInOffline ->
            with(binding) {
                if (isInOffline) {
                    briefingsListContainer.isEnabled = false
                } else {
                    briefingsListContainer.setOnRefreshListener {
                        mainViewModel.loadOnlineData()
                    }
                }
            }
        }
        // current view model
        viewModel.filteredBriefings.observe(viewLifecycleOwner) { briefings ->
            adapter.setBriefings(briefings)
        }
    }

    private fun onBriefingClick(briefing: BriefingUi) {
        hostCallbacks.openBriefingDetails(briefing = briefing)
    }

    companion object {

        private const val ARG_CATEGORY_ID: String = "category_id"
        private const val ARG_TYPE_ID: String = "type_id"

        fun newInstance(categoryId: Int, typeId: Int): BriefingsListFragment =
            BriefingsListFragment().apply {
                arguments = bundleOf(
                    ARG_CATEGORY_ID to categoryId,
                    ARG_TYPE_ID to typeId,
                )
            }
    }
}
