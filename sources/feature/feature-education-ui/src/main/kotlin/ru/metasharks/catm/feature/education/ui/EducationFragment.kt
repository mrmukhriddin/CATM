package ru.metasharks.catm.feature.education.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.education.ui.databinding.FragmentEducationBinding

@AndroidEntryPoint
class EducationFragment : Fragment(R.layout.fragment_education) {

    private lateinit var toolbarCallback: ToolbarCallback

    private val binding: FragmentEducationBinding by viewBinding()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolbarCallback = requireListener()
    }

    private val client = object : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            if (view.canGoBack()) {
                toolbarCallback.showBack(true) {
                    view.goBack()
                }
            }
            super.onPageFinished(view, url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (!view.canGoBack()) {
                toolbarCallback.showBack(false, null)
            }
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
    }

    private fun setupWebView() {
        binding.root.apply {
            webViewClient = client
            settings.javaScriptEnabled = true
            loadUrl("https://sdo.sekonsalt.ru/")
        }
    }
}
