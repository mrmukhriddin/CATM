package ru.metasharks.catm.media.preview.pdf

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rajat.pdfviewer.PdfRendererView
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.media.preview.R
import ru.metasharks.catm.media.preview.databinding.FragmentPdfBinding
import ru.metasharks.catm.utils.argumentDelegate
import timber.log.Timber
import java.io.FileNotFoundException
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.fragment_pdf) {

    @Inject
    lateinit var fileManager: FileManager

    private val fileUri: String by argumentDelegate(ARG_FILE_URI)
    private val binding: FragmentPdfBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(fileUri)
        processFileUri(fileUri)
    }

    private fun processFileUri(fileUri: String) {
        when {
            fileUri.startsWith(CONTENT_PREFIX) -> {
                val file = try {
                    fileManager.getFileFromUri(fileUri.toUri())
                } catch (ex: FileNotFoundException) {
                    Toast.makeText(requireContext(), "Файл не найден", Toast.LENGTH_SHORT).show()
                    return
                }
                binding.pdfView.initWithFile(file)
            }
            else -> {
                binding.pdfView.apply {
                    initWithUrl(fileUri)
                    statusListener = object : PdfRendererView.StatusCallBack {

                        override fun onError(error: Throwable) {
                            Timber.e(error)
                            Toast.makeText(requireContext(), error.message!!, Toast.LENGTH_SHORT)
                                .show()
                            binding.downloadIndicator.isGone = true
                        }

                        override fun onDownloadStart() {
                            binding.downloadIndicator.isVisible = true
                        }

                        override fun onDownloadSuccess() {
                            binding.downloadIndicator.isGone = true
                        }
                    }
                }
            }
        }
    }

    companion object {

        private const val CONTENT_PREFIX = "content"

        private const val ARG_FILE_URI = "file_uri"

        fun newInstance(fileUri: String): PdfFragment {
            val args = Bundle()

            args.putString(ARG_FILE_URI, fileUri)
            val fragment = PdfFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
