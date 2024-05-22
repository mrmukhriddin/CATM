package ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.feature.createworkpermit.ui.R
import ru.metasharks.catm.feature.createworkpermit.ui.databinding.FragmentAttachmentBinding
import ru.metasharks.catm.feature.createworkpermit.ui.steps.StepFragment
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.strings.getSizeString

@AndroidEntryPoint
class AttachmentFragment : StepFragment(R.layout.fragment_attachment) {

    private val binding: FragmentAttachmentBinding by viewBinding()
    private val viewModel: AttachmentViewModel by viewModels()

    private val restoreData: AttachmentRestoreData? by argumentDelegate(ARG_RESTORE_DATA)

    val fileInfo: AttachmentViewModel.AttachmentInfo?
        get() = viewModel.fileInfo.value

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                setFile(uri)
            }
        }

    private fun setFile(uri: Uri) {
        viewModel.loadFileByUri(uri)
    }

    private fun showChooseFile(chooseFile: Boolean) {
        if (chooseFile) {
            binding.uploadFileArea.isVisible = true
            binding.fileUploadedContainer.isGone = true
        } else {
            binding.uploadFileArea.isGone = true
            binding.fileUploadedContainer.isVisible = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()

        restoreData?.let { viewModel.restore(it) }
    }

    private fun setupObservers() {
        viewModel.fileInfo.observe(viewLifecycleOwner) { fileInfo ->
            setFileInfo(fileInfo)
        }
        viewModel.warning.observe(viewLifecycleOwner) { warning ->
            CustomSnackbar.make(binding.uploadFileArea, warning, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setFileInfo(fileInfo: AttachmentViewModel.AttachmentInfo?) {
        showChooseFile(fileInfo == null)
        if (fileInfo == null) {
            binding.nextBtn.isEnabled = false
        } else {
            val sizeString = getSizeString(fileInfo.size)
            binding.fileName.text = fileInfo.name
            binding.fileTypeAndSize.text = sizeString
            binding.nextBtn.isEnabled = true
        }
    }

    private fun setupUi() {
        binding.nextBtn.setOnClickListener {
            stepCallback.endStep(gainOutputData(), gainRestoreData())
        }
        binding.backBtn.setOnClickListener {
            stepCallback.back()
        }
        binding.removeFileBtn.setOnClickListener {
            viewModel.clearFileInfo()
        }
        binding.uploadFileArea.setOnClickListener {
            getContent.launch("application/pdf")
        }
    }

    private fun gainOutputData(): AttachmentStepOutput {
        return AttachmentStepOutput(requireNotNull(fileInfo).uri)
    }

    private fun gainRestoreData(): AttachmentRestoreData {
        return AttachmentRestoreData(requireNotNull(fileInfo))
    }

    @Parcelize
    class AttachmentRestoreData(
        val attachmentInfo: AttachmentViewModel.AttachmentInfo
    ) : RestoreData()

    companion object {

        fun newInstance(restoreData: RestoreData?): AttachmentFragment {
            return AttachmentFragment().apply {
                arguments = bundleOf(ARG_RESTORE_DATA to restoreData)
            }
        }
    }
}
