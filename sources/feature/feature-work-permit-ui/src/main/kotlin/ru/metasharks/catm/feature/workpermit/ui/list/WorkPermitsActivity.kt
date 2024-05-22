package ru.metasharks.catm.feature.workpermit.ui.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.utils.hideAndShow
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData.Companion.countNonNullItemsCount
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData.Companion.isEmpty
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitsBinding
import ru.metasharks.catm.feature.workpermit.ui.filter.GetFilters
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.setDefiniteMargin
import javax.inject.Inject

@AndroidEntryPoint
internal class WorkPermitsActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val binding: ActivityWorkPermitsBinding by viewBinding(CreateMethod.INFLATE)
    private val adapter: WorkPermitsAdapter by lazy {
        WorkPermitsAdapter(this::loadNewPage, this::onWorkPermitClick)
    }

    private val viewModel: WorkPermitsViewModel by viewModels()

    private val getFiltersContract =
        registerForActivityResult(GetFilters()) { (filterOutput, code) ->
            if (code != GetFilters.CODE_SET) {
                return@registerForActivityResult
            }
            val isFilterEmpty = filterOutput.isEmpty()
            binding.wpFilterAppliedIndicator.isGone = isFilterEmpty
            binding.wpFilterAppliedIndicator.text = filterOutput.countNonNullItemsCount().toString()
            viewModel.loadWorkPermits(filterOutput)
        }

    private fun onWorkPermitClick(workPermitUiMain: WorkPermitUi) {
        viewModel.openWorkPermit(workPermitUiMain.permitId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupObservers()
        setupToolbar()
        setupRecycler()

        viewModel.init()
    }

    private fun setupUi() {
        binding.filtersButton.setOnClickListener {
            getFiltersContract.launch(viewModel.filterData)
        }
        binding.wpCreateWorkPermitBtn.setOnClickListener {
            viewModel.openCreateWorkPermitScreen()
        }
    }

    private fun loadNewPage() {
        viewModel.loadWorkPermits(true)
    }

    private fun setupObservers() {
        viewModel.isInOfflineLiveData.observe(this) { isInOffline ->
            if (isInOffline.not()) { // online
                binding.wpWorkPermitsRecyclerSwipeRefresh.setOnRefreshListener {
                    viewModel.loadWorkPermits(false)
                }
            } else { // offline
                binding.filtersButton.isGone = true
                binding.wpWorkPermitsRecyclerSwipeRefresh.isEnabled = false
            }
        }
        viewModel.role.observe(this) {
            setupRole(it)
        }
        viewModel.workPermits.observe(this) { workPermits ->
            if (binding.wpWorkPermitsRecyclerSwipeRefresh.isRefreshing) {
                binding.wpWorkPermitsRecyclerSwipeRefresh.isRefreshing = false
            }
            adapter.items = workPermits
        }
        viewModel.statuses.observe(this) { statuses ->
            if (statuses.isNotEmpty()) {
                binding.tabLayoutPlaceholder.root.hideAndShow(binding.wpTabLayout) {
                    binding.tabLayoutPlaceholder.root.stopShimmer()
                }
            } else {
                binding.tabLayoutPlaceholder.root.stopShimmer()
            }
            setupTabLayoutWithStatusItems(statuses)
            viewModel.loadWorkPermits(statuses.first().statusCode, false)
        }
        viewModel.error.observe(this) { error ->
            errorHandler.handle(binding.filtersButton, error)
        }
    }

    private fun setupRole(role: Role) {
        when (role) {
            Role.DIRECTOR -> {
                binding.wpCreateWorkPermitBtn.isVisible = true
            }
            else -> {
                binding.wpCreateWorkPermitBtn.isGone = true
            }
        }
    }

    private fun setupRecycler() {
        binding.wpWorkPermitsRecycler.adapter = adapter
        binding.wpWorkPermitsRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarLayout.toolbar,
            showTitle = true,
            title = title.toString()
        )
    }

    private fun setupTabLayoutWithStatusItems(statuses: List<StatusUiItem>) {
        val tabContainer = binding.wpTabLayout.getChildAt(0) as ViewGroup
        statuses.forEachIndexed { index, statusUiItem ->
            binding.wpTabLayout.addTab(createTabItem(statusUiItem))
            if (index > 0) {
                tabContainer.getChildAt(index).apply {
                    setDefiniteMargin(left = dpToPx(SPACE_BETWEEN_TABS_DP))
                }
            }
        }
        binding.wpTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    val statusCode = tab.tag as String
                    viewModel.loadWorkPermits(statusCode, false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) = Unit

                override fun onTabReselected(tab: TabLayout.Tab) = Unit
            }
        )
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    private fun createTabItem(statusUiItem: StatusUiItem): TabLayout.Tab {
        return binding.wpTabLayout.newTab()
            .setTag(statusUiItem.statusCode)
            .setText(statusUiItem.statusLocalizedName)
    }

    companion object {
        private const val SPACE_BETWEEN_TABS_DP = 24

        fun createIntent(context: Context): Intent =
            Intent(context, WorkPermitsActivity::class.java)
    }
}
