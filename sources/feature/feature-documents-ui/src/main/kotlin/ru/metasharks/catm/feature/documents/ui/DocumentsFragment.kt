package ru.metasharks.catm.feature.documents.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultItem
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.core.ui.utils.hideAndShow
import ru.metasharks.catm.core.ui.utils.setupSearch
import ru.metasharks.catm.feature.documents.ui.databinding.FragmentDocumentsBinding
import ru.metasharks.catm.feature.documents.ui.recycler.DocumentsAdapter
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentDirectoryUI
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentUI
import ru.metasharks.catm.utils.dpToPx
import timber.log.Timber

@AndroidEntryPoint
class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private lateinit var toolbarCallback: ToolbarCallback

    private val documentsAdapter = DocumentsAdapter(
        this::onDirectoryClick,
        this::onDocumentClick,
        this::onNearEndListener,
    )

    private val searchDocumentsAdapter = DocumentsAdapter(
        this::onDirectoryClick,
        this::onDocumentClick,
        this::onNearEndListener,
    )

    private val viewModel: DocumentsViewModel by viewModels()
    private val binding: FragmentDocumentsBinding by viewBinding()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolbarCallback = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables()
        setupUi()
        viewModel.loadDocumentDirectories(getString(R.string.documents_label))
    }

    private fun onDirectoryClick(item: DocumentDirectoryUI) {
        viewModel.loadDocumentsForType(item.typeId, false)
    }

    private fun onDocumentClick(item: DocumentUI) {
        viewModel.openFile(item.fileUri)
    }

    private fun setupUi() {
        with(binding) {
            documentsRecycler.apply {
                adapter = documentsAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext().dpToPx(SPACE_DP),
                        orientation = DividerItemDecoration.VERTICAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                    )
                )
            }
            searchDocumentsRecycler.apply {
                adapter = searchDocumentsAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext().dpToPx(SPACE_DP),
                        orientation = DividerItemDecoration.VERTICAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                    )
                )
                searchDocumentsAdapter.items = listOf(EmptySearchResultItem())
            }
        }
        setupSearch()
    }

    private fun setupSearch() {
        binding.searchInput
            .setupSearch()
            .subscribe(
                { searchQuery ->
                    viewModel.setNewSearchQuery(searchQuery)
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun setupObservables() {
        viewModel.currentDirectoryName.observe(viewLifecycleOwner) { directoryName ->
            if (directoryName == null) return@observe
            binding.title.text = directoryName
        }
        viewModel.adapterItems.observe(viewLifecycleOwner) { documents ->
            if (documents == null) return@observe
            documentsAdapter.items = documents
        }
        viewModel.adapterSearchItems.observe(viewLifecycleOwner) { documents ->
            if (documents == null) return@observe
            searchDocumentsAdapter.items = documents
        }
        viewModel.loadingSearch.observe(viewLifecycleOwner) { loadingSearch ->
            setLoadingSearchIndicator(loadingSearch)
        }
        viewModel.currentDirectoryId.observe(viewLifecycleOwner) { currentDirectoryId ->
            if (currentDirectoryId != DocumentsViewModel.MAIN) {
                binding.searchInputContainer.isVisible = true
                toolbarCallback.showBack(true) {
                    if (binding.searchInput.text.isNullOrEmpty()) {
                        viewModel.displayPrevious()
                    } else {
                        binding.searchInput.setText("")
                    }
                }
            } else {
                binding.searchInputContainer.isGone = true
                toolbarCallback.showBack(false, null)
            }
        }
        viewModel.inSearch.observe(viewLifecycleOwner) { inSearch ->
            showSearch(inSearch)
        }
    }

    private fun setLoadingSearchIndicator(loadingSearch: Boolean) {
        binding.searchProgress.isVisible = loadingSearch
    }

    private fun showSearch(show: Boolean) {
        if (show) {
            if (!binding.searchDocumentsRecycler.isVisible) {
                binding.documentsRecycler.hideAndShow(binding.searchDocumentsRecycler)
            }
        } else {
            if (!binding.documentsRecycler.isVisible) {
                binding.searchDocumentsRecycler.hideAndShow(binding.documentsRecycler)
            }
        }
    }

    private fun onNearEndListener() {
        viewModel.loadNewPage()
    }

    companion object {

        private const val SPACE_DP = 16
    }
}
