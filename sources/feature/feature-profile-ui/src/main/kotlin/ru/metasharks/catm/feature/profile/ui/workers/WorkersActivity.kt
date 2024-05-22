package ru.metasharks.catm.feature.profile.ui.workers

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.core.ui.utils.hideAndShow
import ru.metasharks.catm.core.ui.utils.setupSearch
import ru.metasharks.catm.feature.profile.ui.R
import ru.metasharks.catm.feature.profile.ui.databinding.ActivityWorkersBinding
import ru.metasharks.catm.feature.profile.ui.workers.recycler.WorkerUI
import ru.metasharks.catm.feature.profile.ui.workers.recycler.WorkersAdapter
import ru.metasharks.catm.utils.dpToPx
import timber.log.Timber

@AndroidEntryPoint
class WorkersActivity : BaseActivity() {

    private val viewModel: WorkerViewModel by viewModels()
    private val binding: ActivityWorkersBinding by viewBinding(CreateMethod.INFLATE)

    private val workersAdapter = WorkersAdapter(
        this::onWorkerClick,
        this::onNearTheEnd,
    )

    private val searchWorkersAdapter = WorkersAdapter(
        this::onWorkerClick,
        this::onNearTheEnd,
    )

    private fun onNearTheEnd() {
        viewModel.loadNewPage()
    }

    private fun onWorkerClick(workerUI: WorkerUI) {
        viewModel.openProfile(workerUI.userId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupObservers()

        viewModel.load()
    }

    private fun setupUi() {
        setToolbar(binding.toolbarLayout.toolbar, showTitle = true, titleRes = R.string.workers)
        with(binding.workersRecycler) {
            adapter = workersAdapter
            addItemDecoration(
                DividerItemDecoration(
                    dpToPx(RECYCLER_SPACE_DP),
                    orientation = DividerItemDecoration.VERTICAL
                )
            )
        }
        with(binding.searchWorkersRecycler) {
            adapter = searchWorkersAdapter
            addItemDecoration(
                DividerItemDecoration(
                    dpToPx(RECYCLER_SPACE_DP),
                    orientation = DividerItemDecoration.VERTICAL
                )
            )
        }
        binding.workersRecyclerRefreshContainer.setOnRefreshListener {
            viewModel.load()
        }
        setupSearch()
    }

    private fun setupSearch() {
        binding.nameInput
            .setupSearch()
            .subscribe(
                { message ->
                    viewModel.setSearch(message)
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    private fun setupObservers() {
        viewModel.adapterItems.observe(this) { adapterItems ->
            binding.workersRecyclerRefreshContainer.isRefreshing = false
            workersAdapter.items = adapterItems
        }
        viewModel.isInOfflineLiveData.observe(this) { isInOffline ->
            binding.workersRecyclerRefreshContainer.isEnabled = isInOffline.not()
        }
        viewModel.adapterSearchItems.observe(this) { adapterItems ->
            if (adapterItems == null) return@observe
            searchWorkersAdapter.items = adapterItems
        }
        viewModel.loadingSearch.observe(this) { loadingSearch ->
            setLoadingSearchIndicator(loadingSearch)
        }
        viewModel.inSearch.observe(this) { sq ->
            showSearch(sq)
        }
    }

    private fun setLoadingSearchIndicator(loadingSearch: Boolean) {
        binding.searchProgress.isVisible = loadingSearch
    }

    private fun showSearch(show: Boolean) {
        if (show) {
            if (!binding.searchWorkersRecycler.isVisible) {
                binding.workersRecyclerRefreshContainer.hideAndShow(binding.searchWorkersRecycler)
            }
        } else {
            if (!binding.workersRecycler.isVisible) {
                binding.searchWorkersRecycler.hideAndShow(binding.workersRecyclerRefreshContainer)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    companion object {

        private const val RECYCLER_SPACE_DP = 16
    }
}
