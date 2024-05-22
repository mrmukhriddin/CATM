package ru.metasharks.catm.feature.feedback.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.KeyboardCallback
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.fragment.findListener
import ru.metasharks.catm.feature.feedback.ui.databinding.FragmentFeedbackBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

@AndroidEntryPoint
internal class FeedbackFragment : Fragment(R.layout.fragment_feedback) {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private var keyboardCallback: KeyboardCallback? = null

    private val binding: FragmentFeedbackBinding by viewBinding()
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        keyboardCallback = findListener()
    }

    private val steps by lazy {
        listOf(
            Field.Text(
                tag = TAG_THEME,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_theme),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.Text(
                tag = TAG_EMAIL,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_email),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input)
                )
            ),
            Field.Text(
                tag = TAG_MESSAGE,
                input = Field.Text.Input(
                    label = StringResWrapper(R.string.field_label_content),
                    hint = StringResWrapper(ru.metasharks.catm.feature.step.R.string.field_hint_input),
                    oneLine = false,
                )
            ),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUi()
    }

    private fun setupObservers() {
        viewModel.sent.observe(viewLifecycleOwner) {
            showSuccessDialog()
            keyboardCallback?.hideKeyboard()
            binding.feedbackForm.clearFields()
            binding.sendFeedbackBtn.isLoading = false
        }
        viewModel.error.observe(viewLifecycleOwner) {
            errorHandler.handle(binding.root, it)
            binding.sendFeedbackBtn.isLoading = false
        }
    }

    private fun showSuccessDialog() {
        ConfirmationDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_feedback_success)
            .setPositiveButtonText(R.string.dialog_btn_positive_feedback_success)
            .setNegativeButtonShow(false)
            .show()
    }

    private fun setupUi() {
        with(binding) {
            feedbackForm.setOnFormFilledListener {
                sendFeedbackBtn.isEnabled = it
            }
            sendFeedbackBtn.setOnClickListener {
                if (feedbackForm.validate().isValid) {
                    sendFeedbackBtn.isLoading = true
                    viewModel.sendFeedback(gatherData())
                }
            }
            feedbackForm.inflateOf(steps, setNecessaryItems = true)
        }
    }

    private fun gatherData(): FeedbackViewModel.FeedbackData {
        val data = binding.feedbackForm.gatherData()
        return FeedbackViewModel.FeedbackData(
            theme = data[TAG_THEME] as String,
            email = data[TAG_EMAIL] as String,
            message = data[TAG_MESSAGE] as String,
        )
    }

    companion object {

        private const val TAG_THEME = "theme"
        private const val TAG_EMAIL = "email"
        private const val TAG_MESSAGE = "message"
    }
}
