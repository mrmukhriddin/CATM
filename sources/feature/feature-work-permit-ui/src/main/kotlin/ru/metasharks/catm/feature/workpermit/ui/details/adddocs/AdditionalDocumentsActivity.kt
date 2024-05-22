package ru.metasharks.catm.feature.workpermit.ui.details.adddocs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ActivityWorkPermitDetialsAdditionalDocumentsBinding
import ru.metasharks.catm.feature.workpermit.ui.details.adddocs.recycler.AdditionalDocumentsAdapter
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx

@AndroidEntryPoint
class AdditionalDocumentsActivity : BaseActivity() {

    private val workPermitId: Long by argumentDelegate(EXTRA_WORK_PERMIT_ID)

    private val documentsAdapter: AdditionalDocumentsAdapter by lazy {
        AdditionalDocumentsAdapter(this::onDocumentClick, this::onDeleteClick)
    }

    private val binding: ActivityWorkPermitDetialsAdditionalDocumentsBinding by viewBinding(
        CreateMethod.INFLATE
    )
    private val viewModel: AdditionalDocumentsViewModel by viewModels()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.loadFileByUri(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupObservers()

        viewModel.load(workPermitId)
    }

    private fun onDocumentClick(document: DocumentUi) {
        viewModel.openDocument(document.fileUrl)
    }

    private fun onDeleteClick(document: DocumentUi) {
        viewModel.deleteFileFromServer(document)
    }

    private fun setupUi() {
        setToolbar(binding.toolbarContainer.toolbar, showTitle = true, title = title.toString())
        with(binding) {
            with(documentsRecycler) {
                adapter = documentsAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        dpToPx(SPACE_DP),
                        orientation = DividerItemDecoration.VERTICAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END
                    )
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.workPermit.observe(this) { workPermit ->
            if (workPermit.isCreator && workPermit.status == StatusCode.NEW) {
                binding.uploadFileZone.isVisible = true
                binding.uploadFileZone.setOnClickListener { getContent.launch("application/pdf") }
                documentsAdapter.setDeleteAvailable(true)
            } else {
                documentsAdapter.setDeleteAvailable(false)
                binding.uploadFileZone.isGone = true
            }
            documentsAdapter.items = workPermit.additionalDocuments
        }
        viewModel.warning.observe(this) { warningText ->
            CustomSnackbar.make(binding.uploadFileZone, warningText, Snackbar.LENGTH_LONG).show()
        }
        viewModel.fileInfo.observe(this) { attachmentInfo ->
            if (attachmentInfo == null) {
                return@observe
            }
            viewModel.uploadFileToServer(attachmentInfo)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.uploadFileZone.text = ""
                binding.indicatorLoading.isVisible = true
                binding.indicatorLoading.isIndeterminate = true
            } else {
                binding.uploadFileZone.setText(R.string.choose_and_upload_file_label)
                binding.indicatorLoading.isGone = true
                binding.indicatorLoading.isIndeterminate = false
            }
        }
        viewModel.fileLoaded.observe(this) { (file, load) ->
            if (load) {
                documentsAdapter.addItem(file)
            } else {
                documentsAdapter.deleteItem(file)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val SPACE_DP = 20

        private const val EXTRA_WORK_PERMIT_ID = "work_permit_id"

        fun createIntent(context: Context, workPermitId: Long): Intent {
            return Intent(context, AdditionalDocumentsActivity::class.java)
                .putExtra(EXTRA_WORK_PERMIT_ID, workPermitId)
        }
    }
}
