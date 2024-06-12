package ru.metasharks.catm.sources.feature.agree.data.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.feature.agree.data.ui.R
import ru.metasharks.catm.feature.agree.data.ui.databinding.ActivityAgreeDataBinding
import javax.inject.Inject

@AndroidEntryPoint
internal class AgreeDataActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val viewModel: AgreeDataViewModel by viewModels()
    private val binding: ActivityAgreeDataBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        setupUi()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) {
            // Implement loading logic (e.g., show/hide a progress bar)
        }
        viewModel.error.observe(this) {
            errorHandler.handle(binding.root, it)
        }
        viewModel.errorDetail.observe(this) { message ->
            if (message != null) {
                CustomSnackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }
        viewModel.verificationSuccess.observe(this) { success ->
            if (success) {

            } else {
            }
        }
    }

    private fun setupUi() {
        setContentView(binding.root)
        binding.actionLoginBtn.setOnClickListener {
            val suffix = binding.editSuffix.text.toString()
            if (suffix.isNotEmpty()) {
                viewModel.verifySuffix(suffix)
            } else {
                CustomSnackbar.make(binding.root, "Please enter a suffix", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.exit()

    }
}