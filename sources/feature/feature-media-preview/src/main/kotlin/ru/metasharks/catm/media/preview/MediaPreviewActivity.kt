package ru.metasharks.catm.media.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.media.preview.databinding.ActivityMediaPreviewBinding
import ru.metasharks.catm.media.preview.pdf.PdfFragment
import ru.metasharks.catm.media.preview.photo.PhotoFragment
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.strings.getExtension

@AndroidEntryPoint
internal class MediaPreviewActivity : BaseActivity() {

    private val viewModel: MediaPreviewViewModel by viewModels()
    private val binding: ActivityMediaPreviewBinding by viewBinding(CreateMethod.INFLATE)

    private val fileUri: String by argumentDelegate(EXTRA_FILE_URI)
    private val titleString: String? by argumentDelegate(EXTRA_TITLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupContent()
    }

    private fun setupToolbar() {
        val setTitle = titleString != null
        setToolbar(binding.toolbarContainer.toolbar, showTitle = setTitle, title = titleString)
    }

    private fun setupContent() {
        when (determineTypeOfFile(fileUri)) {
            TYPE_IMAGE -> {
                supportFragmentManager.beginTransaction()
                    .add(binding.fragmentContainer.id, PhotoFragment.newInstance(fileUri), TYPE_PDF)
                    .commit()
            }
            TYPE_PDF -> {
                supportFragmentManager.beginTransaction()
                    .add(binding.fragmentContainer.id, PdfFragment.newInstance(fileUri), TYPE_PDF)
                    .commit()
            }
            else -> throw IllegalArgumentException("Unsupported type of content")
        }
    }

    private fun determineTypeOfFile(fileUri: String): String {
        val extension = getExtension(fileUri)
        return when (extension) {
            "jpg", "png" -> TYPE_IMAGE
            "pdf" -> TYPE_PDF
            else -> TYPE_UNDEFINED
        }
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    companion object {

        private const val TYPE_PDF = "pdf"
        private const val TYPE_IMAGE = "img"
        private const val TYPE_UNDEFINED = "?"

        private const val EXTRA_FILE_URI = "file_uri"
        private const val EXTRA_TITLE = "title"

        fun createIntent(context: Context, fileUri: String, title: String?): Intent {
            return Intent(context, MediaPreviewActivity::class.java)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .putExtra(EXTRA_TITLE, title)
        }
    }
}
