package ru.metasharks.catm.feature.aboutapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.utils.ClipboardUiUtils
import ru.metasharks.catm.feature.aboutapp.ui.databinding.ActivityAboutAppBinding

@AndroidEntryPoint
internal class AboutAppActivity : BaseActivity() {

    private val binding: ActivityAboutAppBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: AboutAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupUi()
    }

    private fun setupUi() {
        binding.authorPhone.setOnClickListener { view ->
            ClipboardUiUtils.copyPhoneNumber(
                view,
                requireNotNull(binding.authorPhone.text.toString())
            )
        }
        binding.distributorPhone.setOnClickListener { view ->
            ClipboardUiUtils.copyPhoneNumber(
                view,
                requireNotNull(binding.distributorPhone.text.toString())
            )
        }
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showTitle = true,
            title = title.toString(),
            showNavigate = true
        )
    }

    override fun onBackPressed() {
        viewModel.exit()
    }
}
