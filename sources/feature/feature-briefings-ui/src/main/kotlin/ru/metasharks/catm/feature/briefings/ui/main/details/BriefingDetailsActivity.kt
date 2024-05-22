package ru.metasharks.catm.feature.briefings.ui.main.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.databinding.ActivityBriefingDetailsBinding

@AndroidEntryPoint
internal class BriefingDetailsActivity : BaseActivity() {

    private val viewModel: BriefingDetailsViewModel by viewModels()
    private val binding: ActivityBriefingDetailsBinding by viewBinding(CreateMethod.INFLATE)
    private val briefingId: Int by lazy { intent.getIntExtra(EXTRA_BRIEFING_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialData(briefingId)
        setupUi()
        setupObservers()
        viewModel.init()
    }

    private fun setupUi() {
        setContentView(binding.root)
        setupToolbar()
        with(binding) {
            briefingQuizBtn.setOnClickListener {
                viewModel.openBriefingQuiz()
            }
            briefingSignBtn.setOnClickListener {
                viewModel.signBriefing()
            }
            // disable scroll on touch
            with(briefingContentWebView) {
                setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }
                isScrollContainer = false
                with(settings) {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                }
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        view.loadUrl("javascript:document.body.style.margin=\"8%\"; void 0")
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.payload.observe(this) { payload ->
            when (payload) {
                is BriefingDetailsViewModel.Payload.OutgoingSigning -> {
                    binding.briefingSignBtn.isEnabled = false
                    binding.warningText.isVisible = true
                    binding.warningText.text =
                        getString(R.string.briefing_awaiting_internet_connection)
                }
                is BriefingDetailsViewModel.Payload.OutgoingQuiz -> {
                    binding.briefingQuizBtn.isEnabled = false
                    binding.warningText.isVisible = true
                    binding.warningText.text =
                        getString(R.string.briefing_awaiting_internet_connection)
                }
            }
        }
        viewModel.signLoading.observe(this) { loading ->
            binding.briefingSignBtn.isLoading = loading
        }
        viewModel.briefing.observe(this) { briefing ->
            binding.briefingTitle.text = briefing.text
            showHtmlContent(briefing.content)

            briefing.fileData?.let { fileData ->
                if (binding.briefingFileStub.parent == null) {
                    return@let
                }
                val layoutDocument = binding.briefingFileStub.inflate()
                layoutDocument.findViewById<TextView>(ru.metasharks.catm.core.ui.R.id.file_name).text =
                    fileData.name
                layoutDocument.findViewById<TextView>(ru.metasharks.catm.core.ui.R.id.file_type_and_size).text =
                    fileData.sizeAndExtension
                layoutDocument.setOnClickListener { viewModel.openFile(fileData.url) }
            }
            briefing.bottomActions.let { bottomActions ->
                bottomActions.bottomResultsText?.let { resultsText ->
                    binding.briefingResultsText.text = resultsText
                    binding.briefingResultsText.isVisible = true
                } ?: run {
                    binding.briefingResultsText.isGone = true
                }
                binding.briefingQuizBtn.isVisible = bottomActions.bottomQuizButtonVisible
                binding.briefingSignBtn.isVisible = bottomActions.bottomSignButtonVisible
                binding.briefingSignBtn.isEnabled = bottomActions.bottomSignButtonEnabled
            }
        }
    }

    private fun showHtmlContent(content: String) {
        val mimeType = "text/html"
        val encoding = "UTF-8"
        val sb =
            StringBuilder()
                .append("<style>img{display: inline; height: auto; max-width: 100%;}</style>")
                .append(content)
        binding.briefingContentWebView.loadDataWithBaseURL(
            "",
            sb.toString(),
            mimeType,
            encoding,
            ""
        )
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showTitle = false,
            showNavigate = true
        )
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val EXTRA_BRIEFING_ID: String = "briefing_id"

        fun createIntent(context: Context, briefingId: Int): Intent {
            return Intent(context, BriefingDetailsActivity::class.java)
                .putExtra(EXTRA_BRIEFING_ID, briefingId)
        }
    }
}
