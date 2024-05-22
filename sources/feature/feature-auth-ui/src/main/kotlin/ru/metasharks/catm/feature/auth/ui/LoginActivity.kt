package ru.metasharks.catm.feature.auth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.core.ui.utils.textStringOrEmpty
import ru.metasharks.catm.feature.auth.ui.databinding.ActivityLoginBinding
import ru.metasharks.catm.feature.notifications.NotificationsService
import ru.metasharks.catm.utils.FeatureToggle
import ru.metasharks.catm.utils.argumentDelegate
import javax.inject.Inject

@AndroidEntryPoint
internal class LoginActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private var switchBaseUrlCounter = 0

    private val viewModel: LoginViewModel by viewModels()
    private val binding: ActivityLoginBinding by viewBinding(CreateMethod.INFLATE)

    private val isUserSession: Boolean? by argumentDelegate(EXTRA_IS_USER_SESSION)
    private val isTokenExpired: Boolean? by argumentDelegate(EXTRA_IS_TOKEN_EXPIRED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        viewModel.checkIfAlreadyLogged(isUserSession)
    }

    private fun checkIfTokenExpired() {
        if (isTokenExpired == true) {
            CustomSnackbar.make(
                binding.root,
                R.string.warning_token_expired,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) {
            binding.actionLoginBtn.isLoading = it
        }
        viewModel.error.observe(this) {
            errorHandler.handle(binding.root, it)
            binding.actionLoginBtn.isLoading = false
        }
        viewModel.errorDetail.observe(this) { message ->
            CustomSnackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
        viewModel.setupUi.observe(this) {
            if (it) {
                checkIfTokenExpired()
                startService(NotificationsService.createStopIntent(this))
                setupUi()
            }
        }
    }

    private fun setupUi() {
        setContentView(binding.root)
        binding.actionLoginBtn.setOnClickListenerAndLoad {
            if (checkInputsNotEmpty())
                viewModel.login(
                    binding.usernameInput.textStringOrEmpty,
                    binding.passwordInput.textStringOrEmpty
                )
        }
        binding.aboutAppBtn.setOnClickListener {
            viewModel.openAboutAppScreen()
        }
        if (FeatureToggle.SHOW_ROLES.isEnabled) {
            setupRolesButtons()
        }
        if (FeatureToggle.DEMO_URL.isEnabled) {
            setupSwitchBaseUrlButton()
        }
    }

    private fun checkInputsNotEmpty(): Boolean {
        var returnVar = true
        if (binding.usernameInput.textStringOrEmpty.isEmpty()) {
            returnVar = false
            CustomSnackbar.make(
                binding.root,
                R.string.error_input_empty_username,
                Snackbar.LENGTH_LONG
            ).show()
        } else if (binding.passwordInput.textStringOrEmpty.isEmpty()) {
            returnVar = false
            CustomSnackbar.make(
                binding.root,
                R.string.error_input_empty_password,
                Snackbar.LENGTH_LONG
            ).show()
        }
        if (!returnVar) {
            binding.actionLoginBtn.isLoading = false
        }
        return returnVar
    }

    // Used in test purposes: enabling/disabling in FeatureToggle
    @Suppress("MagicNumber")
    private fun setupSwitchBaseUrlButton() {
        binding.demo.isVisible = viewModel.currentUrlState()
        binding.catmLogo.setOnClickListener {
            if (++switchBaseUrlCounter == 5) {
                binding.demo.isVisible = viewModel.switchBaseUrl()
                CustomSnackbar.make(
                    binding.catmLogo,
                    "Чтобы изменения вступили в силу, перезапустите приложение",
                    Snackbar.LENGTH_SHORT
                ).show()
                switchBaseUrlCounter = 0
            }
        }
    }

    // Used in test purposes: enabling/disabling in FeatureToggle
    private fun setupRolesButtons() {
        with(binding) {
            rolesContainer.isVisible = true
            workerBtn.setOnClickListener {
                setText(
                    usernameInput.editText!!,
                    passwordInput.editText!!,
                    "bikov",
                    "bikov"
                )
            }
            directorBtn.setOnClickListener {
                setText(
                    usernameInput.editText!!,
                    passwordInput.editText!!,
                    "rukovodutel@test.test",
                    "rukovodutel"
                )
            }
            observerBtn.setOnClickListener {
                setText(
                    usernameInput.editText!!,
                    passwordInput.editText!!,
                    "poluboff@test.test",
                    "poluboff"
                )
            }
            securityManagerBtn.setOnClickListener {
                setText(
                    usernameInput.editText!!,
                    passwordInput.editText!!,
                    "shchurov@test.test",
                    "shchurov"
                )
            }
        }
    }

    private fun setText(
        usernameView: EditText,
        passwordView: EditText,
        username: String,
        password: String
    ) {
        usernameView.setText(username)
        passwordView.setText(password)
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val EXTRA_IS_USER_SESSION = "is_user_session"
        private const val EXTRA_IS_TOKEN_EXPIRED = "is_token_expired"

        fun createIntent(context: Context, isUserSession: Boolean): Intent {
            return Intent(context, LoginActivity::class.java)
                .putExtra(EXTRA_IS_USER_SESSION, isUserSession)
        }

        fun createIntentAfterTokenExpired(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
                .putExtra(EXTRA_IS_TOKEN_EXPIRED, true)
        }
    }
}
