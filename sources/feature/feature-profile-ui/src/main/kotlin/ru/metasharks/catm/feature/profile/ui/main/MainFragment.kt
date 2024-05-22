package ru.metasharks.catm.feature.profile.ui.main

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.NotificationsWidgetCallback
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.core.ui.utils.hideAndShow
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.entities.SaveType
import ru.metasharks.catm.feature.profile.role.Role
import ru.metasharks.catm.feature.profile.ui.R
import ru.metasharks.catm.feature.profile.ui.databinding.FragmentMainScreenBinding
import ru.metasharks.catm.feature.profile.ui.entities.UserUI
import ru.metasharks.catm.utils.startApplicationDetailsActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main_screen) {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val viewModel: MainViewModel by viewModels()
    private val binding: FragmentMainScreenBinding by viewBinding()

    lateinit var notificationsCallback: NotificationsWidgetCallback

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGiven ->
            if (permissionGiven) {
                viewModel.openQrScanner()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                CustomSnackbar.make(
                    binding.scanQrBtn,
                    R.string.camera_rationale_text,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                CustomSnackbar.make(
                    binding.scanQrBtn,
                    R.string.camera_settings_text,
                    Snackbar.LENGTH_INDEFINITE
                ).setOnClickListener {
                    requireContext().startApplicationDetailsActivity()
                }.show()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notificationsCallback = requireListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUi()
        viewModel.getUser()
    }

    private fun setupUi() {
        with(binding) {
            profileCoreInfoContainer.setOnClickListener {
                viewModel.openProfile()
            }
            aboutAppBtn.setOnClickListener {
                viewModel.openAboutAppScreen()
            }
            swipeToRefreshContainer.setOnRefreshListener {
                notificationsCallback.updateNotifications()
                swipeToRefreshContainer.isRefreshing = false
            }
        }
    }

    private fun setupObservers() {
        viewModel.payload.observe(viewLifecycleOwner) { payload ->
            processPayload(payload)
        }
        viewModel.qrCode.observe(viewLifecycleOwner) { qrCode ->
            binding.qrCodeImg.setOnClickListener {
                viewModel.startQrCodeActivity(
                    getString(R.string.qr_code_img_preview_title)
                )
            }
            Glide.with(requireContext())
                .load(qrCode)
                .into(binding.qrCodeImg)
            binding.qrCodeShimmer.hideAndShow(
                binding.qrCodeImg,
                viewHiddenAction = { binding.qrCodeShimmer.stopShimmer() })
        }
    }

    private fun processPayload(payload: MainViewModel.Payload) {
        when (payload) {
            is MainViewModel.Payload.Online -> {
                setProfileInfo(payload.user)
                setRole(payload.role)
            }
            is MainViewModel.Payload.Offline -> {
                setProfileInfo(payload.user)
                setRole(payload.role)
                setOptionsEnabled(payload.offlineSaves)
                binding.scanQrBtn.isGone = true
                binding.qrCodeImg.setImageResource(R.drawable.ic_qr_code_inactive)
                binding.qrCodeShimmer.hideAndShow(
                    binding.qrCodeImg,
                    viewHiddenAction = { binding.qrCodeShimmer.stopShimmer() })
            }
            is MainViewModel.Payload.OfflineSavesChanges -> {
                setOptionsEnabled(payload.offlineSaves)
            }
            is MainViewModel.Payload.Error -> {
                errorHandler.handle(binding.root, payload.error)
                if (payload.cause == MainViewModel.EXCEPTION_CAUSE_QR) {
                    binding.qrCodeShimmer.hideAndShow(
                        binding.qrCodeImg,
                        viewHiddenAction = { binding.qrCodeShimmer.stopShimmer() }
                    )
                }
            }
        }
    }

    private fun setOptionsEnabled(offlineSaves: List<OfflineSave>) {
        if (offlineSaves.any { it.typeCode == SaveType.WORKERS.code }.not()) {
            binding.workersBtn.makeOffline()
        }
        if (offlineSaves.any { it.typeCode == SaveType.WORK_PERMITS.code }.not()) {
            binding.workPermitsBtn.makeOffline()
        }
    }

    private fun TextView.makeOffline() {
        isEnabled = false
        background.setTint(
            ContextCompat.getColor(
                requireContext(),
                ru.metasharks.catm.core.ui.R.color.bright_gray
            )
        )
        setTextColor(
            ContextCompat.getColor(
                requireContext(),
                ru.metasharks.catm.core.ui.R.color.light_gray
            )
        )
    }

    private fun setRole(role: Role) {
        when (role) {
            Role.WORKER -> {
                with(binding) {
                    workersBtn.isGone = true
                    workPermitsBtn.isGone = true
                    scanQrBtn.isGone = true
                }
            }
            else -> {
                with(binding) {
                    workersBtn.isVisible = true
                    workPermitsBtn.isVisible = true
                    scanQrBtn.isVisible = true
                    workersBtn.setOnClickListener {
                        viewModel.openWorkers()
                    }
                    workPermitsBtn.setOnClickListener {
                        viewModel.openWorkPermits()
                    }
                    scanQrBtn.setOnClickListener {
                        requestPermissions.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }

    private fun setProfileInfo(user: UserUI) {
        with(binding) {
            profileCoreInfo.profileName.text =
                getFullNameWithLineBreak(user.firstName, user.lastName)
            profileCoreInfo.profilePosition.text = user.position
            setUserAvatar(user)
        }
    }

    private fun setUserAvatar(user: UserUI) {
        binding.profileCoreInfo.profileImageContainer
            .setData(getFullName(user.firstName, user.lastName), user.avatar)
            .load()
    }
}
