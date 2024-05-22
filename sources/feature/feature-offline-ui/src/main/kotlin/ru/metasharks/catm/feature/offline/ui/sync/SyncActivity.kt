package ru.metasharks.catm.feature.offline.ui.sync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.utils.showIndefiniteSnack
import ru.metasharks.catm.core.ui.utils.showLongSnack
import ru.metasharks.catm.core.ui.view.LoadingMaterialButtonWrapper
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.offline.ui.R
import ru.metasharks.catm.feature.offline.ui.databinding.ActivitySyncBinding
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.utils.FeatureToggle
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject

@AndroidEntryPoint
class SyncActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val viewModel: SyncViewModel by viewModels()
    private val binding: ActivitySyncBinding by viewBinding(CreateMethod.INFLATE)

    private val downloadController = DownloadController(
        onChange = { allAvailable ->
            binding.clearAllBtn.isEnabled = allAvailable
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupUi()
        setupObservers()

        viewModel.init()
    }

    private fun setupObservers() {
        viewModel.payload.observe(this) { payload ->
            processPayload(payload)
        }
    }

    @Suppress("LongMethod")
    private fun processPayload(payload: SyncViewModel.Payload) {
        when (payload) {
            is SyncViewModel.Payload.SavedSuccessful -> {
                when (payload.type) {
                    SaveType.PROFILE -> {
                        binding.saveProfileBtn.isLoading = false
                        binding.showLongSnack(R.string.snack_saved_profile)
                        downloadController.setProfile(true)
                    }
                    SaveType.WORKERS -> {
                        binding.saveWorkersBtn.isLoading = false
                        binding.showLongSnack(R.string.snack_saved_workers)
                        downloadController.setWorkers(true)
                    }
                    SaveType.WORK_PERMITS -> {
                        binding.saveWorkPermitsBtn.isLoading = false
                        binding.showLongSnack(R.string.snack_saved_work_permits)
                        downloadController.setWorkPermits(true)
                    }
                    SaveType.BRIEFINGS -> {
                        binding.saveBriefingsBtn.isLoading = false
                        binding.showLongSnack(R.string.snack_saved_briefings)
                        downloadController.setBriefings(true)
                    }
                }
            }
            is SyncViewModel.Payload.Error -> {
                errorHandler.handle(binding.root, payload.error)
            }
            is SyncViewModel.Payload.ErrorOnSave -> {
                errorHandler.handle(binding.root, payload.error)
                when (payload.saveType) {
                    SaveType.PROFILE -> binding.saveProfileBtn.isLoading = false
                    SaveType.WORKERS -> binding.saveWorkersBtn.isLoading = false
                    SaveType.WORK_PERMITS -> binding.saveWorkPermitsBtn.isLoading = false
                    SaveType.BRIEFINGS -> binding.saveBriefingsBtn.isLoading = false
                }
            }
            is SyncViewModel.Payload.OfflineSaves -> {
                if (payload.isOffline) {
                    with(binding) {
                        saveProfileBtn.isEnabled = false
                        saveWorkersBtn.isEnabled = false
                        saveWorkPermitsBtn.isEnabled = false
                        saveBriefingsBtn.isEnabled = false
                    }
                }
                payload.saves.forEach { processOfflineSave(payload.isOffline, it) }
            }
            is SyncViewModel.Payload.ClearedAll -> {
                binding.showLongSnack(R.string.snack_cleared_all)
                with(binding) {
                    clearAllBtn.isLoading = false
                    saveProfileBtn.setText(R.string.btn_save)
                    saveProfileTimestamp.isGone = true
                    saveBriefingsBtn.setText(R.string.btn_save)
                    saveBriefingsTimestamp.isGone = true
                    saveWorkPermitsBtn.setText(R.string.btn_save)
                    saveWorkPermitsTimestamp.isGone = true
                    saveWorkersBtn.setText(R.string.btn_save)
                    saveWorkersTimestamp.isGone = true
                }
            }
            is SyncViewModel.Payload.ClearedPendingRequests -> {
                binding.showIndefiniteSnack("PENDING REQUESTS CLEARED")
            }
            is SyncViewModel.Payload.Role -> {
                when (payload.role) {
                    Role.WORKER -> {
                        binding.workPermitsContainer.isGone = true
                        binding.workersContainer.isGone = true
                    }
                }
            }
        }
    }

    private fun processOfflineSave(isOffline: Boolean, save: OfflineSave) {
        when (save.typeCode) {
            SaveType.PROFILE.code -> {
                setOfflineSave(
                    binding.saveProfileTimestamp,
                    binding.saveProfileBtn,
                    save
                )
            }
            SaveType.WORKERS.code -> {
                setOfflineSave(
                    binding.saveWorkersTimestamp,
                    binding.saveWorkersBtn,
                    save
                )
            }
            SaveType.WORK_PERMITS.code -> {
                setOfflineSave(
                    binding.saveWorkPermitsTimestamp,
                    binding.saveWorkPermitsBtn,
                    save
                )
            }
            SaveType.BRIEFINGS.code -> {
                setOfflineSave(
                    binding.saveBriefingsTimestamp,
                    binding.saveBriefingsBtn,
                    save
                )
            }
        }
    }

    private fun setOfflineSave(
        timestampView: TextView,
        buttonView: LoadingMaterialButtonWrapper,
        save: OfflineSave,
    ) {
        val localDate = LocalDateUtils.fromMillis(save.lastTimeSaved)
        timestampView.isVisible = true
        timestampView.text =
            getString(R.string.pattern_last_update, LocalDateUtils.toString(localDate))
        buttonView.text = getString(R.string.btn_update)
    }

    private fun setupToolbar() {
        setToolbar(binding.toolbarContainer.toolbar, showTitle = false, showNavigate = true)
    }

    private fun setupUi() {
        with(binding) {
            setContentView(root)
            saveProfileBtn.setOnClickListenerAndLoad {
                viewModel.download(SaveType.PROFILE)
                downloadController.setProfile(false)
            }
            saveWorkersBtn.setOnClickListenerAndLoad {
                viewModel.download(SaveType.WORKERS)
                downloadController.setWorkers(false)
            }
            saveWorkPermitsBtn.setOnClickListenerAndLoad {
                viewModel.download(SaveType.WORK_PERMITS)
                downloadController.setWorkPermits(false)
            }
            saveBriefingsBtn.setOnClickListenerAndLoad {
                viewModel.download(SaveType.BRIEFINGS)
                downloadController.setBriefings(false)
            }
            clearAllBtn.setOnClickListenerAndLoad {
                viewModel.clearAll()
            }
            if (FeatureToggle.DELETE_PENDING.isEnabled) {
                clearPendingRequestsBtn.isVisible = true
                clearPendingRequestsBtn.setOnClickListener {
                    viewModel.clearPendingRequests()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (downloadController.allAvailable()) {
            viewModel.exit()
        } else {
            binding.showIndefiniteSnack(R.string.snack_downloading)
        }
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, SyncActivity::class.java)
        }
    }

    class DownloadController(
        val onChange: (Boolean) -> Unit,
        private var workPermits: Boolean = true,
        private var workers: Boolean = true,
        private var briefings: Boolean = true,
        private var profile: Boolean = true,
    ) {

        fun allAvailable(): Boolean {
            return workPermits && workers && briefings && profile
        }

        fun setWorkPermits(enabled: Boolean) {
            workPermits = enabled
            onChange(allAvailable())
        }

        fun setWorkers(enabled: Boolean) {
            workers = enabled
            onChange(allAvailable())
        }

        fun setBriefings(enabled: Boolean) {
            briefings = enabled
            onChange(allAvailable())
        }

        fun setProfile(enabled: Boolean) {
            profile = enabled
            onChange(allAvailable())
        }
    }
}
