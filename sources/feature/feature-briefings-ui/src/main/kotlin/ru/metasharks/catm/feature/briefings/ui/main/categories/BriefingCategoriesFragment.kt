package ru.metasharks.catm.feature.briefings.ui.main.categories

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.databinding.FragmentBriefingsScreenBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingCategoryUi
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainCallbacks
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainViewModel

@AndroidEntryPoint
class BriefingCategoriesFragment : Fragment(R.layout.fragment_briefings_screen) {

    private val viewModel: BriefingCategoriesViewModel by viewModels()
    private val mainViewModel: BriefingMainViewModel
        get() = hostCallbacks.getMainViewModel()
    private val binding: FragmentBriefingsScreenBinding by viewBinding()
    private val adapter = BriefingCategoriesAdapter(::onCategoryClick)
    private lateinit var hostCallbacks: BriefingMainCallbacks

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostCallbacks = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables()
        setupUi()
    }

    private fun setupUi() {
        binding.briefingCategoriesRecycler.let { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservables() {
        mainViewModel.isInOfflineModeLiveData.observe(viewLifecycleOwner) { isInOffline ->
            with(binding) {
                if (isInOffline) {
                    briefingCategoriesContainer.isEnabled = false
                } else {
                    briefingCategoriesContainer.setOnRefreshListener {
                        mainViewModel.loadOnlineData()
                    }
                }
            }
        }
        mainViewModel.briefingCategories.observe(viewLifecycleOwner) { categories ->
            adapter.setCategories(categories)
        }
        mainViewModel.dataLoading.observe(viewLifecycleOwner) { dataLoading ->
            binding.briefingCategoriesContainer.isRefreshing = dataLoading
        }
    }

    private fun onCategoryClick(item: BriefingCategoryUi) {
        hostCallbacks.openBriefingTypes(item.categoryId)
    }
}
