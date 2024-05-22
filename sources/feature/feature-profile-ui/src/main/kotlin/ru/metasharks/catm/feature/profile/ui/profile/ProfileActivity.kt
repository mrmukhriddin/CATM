package ru.metasharks.catm.feature.profile.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.ErrorHandler
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.core.ui.utils.ClipboardUiUtils
import ru.metasharks.catm.core.ui.utils.formatNumber
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.core.ui.utils.hideAndShow
import ru.metasharks.catm.core.ui.utils.setTextOrHideField
import ru.metasharks.catm.core.ui.utils.showShortSnack
import ru.metasharks.catm.feature.profile.ui.R
import ru.metasharks.catm.feature.profile.ui.databinding.ActivityProfileBinding
import ru.metasharks.catm.feature.profile.ui.entities.CommonDocumentUI
import ru.metasharks.catm.feature.profile.ui.entities.ProfileUI
import ru.metasharks.catm.feature.profile.ui.profile.entities.Header
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListFileData
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListSignedData
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListTextData
import ru.metasharks.catm.feature.profile.ui.profile.recycler.PersonalDataAdapter
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val viewModel: ProfileViewModel by viewModels()
    private val binding: ActivityProfileBinding by viewBinding(CreateMethod.INFLATE)
    private val userId: Int? by argumentDelegate(EXTRA_USER_ID)

    private val personalDataAdapter = PersonalDataAdapter(this::onFileClickListener)

    private fun onFileClickListener(fileData: ListFileData) {
        viewModel.openFileData(fileData.fileUri)
    }

    override fun onBackPressed() {
        viewModel.back()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupObservers()
        setupToolbar()
        setupUi()

        viewModel.init(userId)
    }

    private fun setupToolbar() {
        setToolbar(binding.toolbarLayout.toolbar)
    }

    private fun setupUi() {
        with(binding) {
            if (userId == null) {
                profileLogOutBtn.isVisible = true
                placeholder.phProfileLogOutBtn.isVisible = true
                profileLogOutBtn.setOnClickListener {
                    viewModel.logout()
                }
            } else {
                profileLogOutBtn.isGone = true
                placeholder.phProfileLogOutBtn.isGone = true
            }
            contentSwipeToRefresh.setOnRefreshListener {
                viewModel.loadProfile(userId, update = true)
            }
            generateCommonDocBtn.setOnClickListener {
                viewModel.generateCommonDoc()
            }
            personalDataRecycler.apply {
                adapter = personalDataAdapter
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                addItemDecoration(
                    DividerItemDecoration(
                        dpToPx(SPACE_DP),
                        orientation = DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.isInOfflineLiveData.observe(this) { isInOffline ->
            binding.generateCommonDocBtn.isEnabled = isInOffline.not()
            binding.contentSwipeToRefresh.isEnabled = isInOffline.not()
        }
        viewModel.profileInfo.observe(this) { profile ->
            binding.contentSwipeToRefresh.isRefreshing = false
            setupProfileInfo(profile)
        }
        viewModel.error.observe(this) { error ->
            errorHandler.handle(binding.root, error)
        }
        viewModel.codeToProfile.observe(this) { (updateCode, profile) ->
            when (updateCode) {
                ProfileViewModel.UPDATE_COMMON_DOC -> {
                    setCommonDoc(profile.commonDocument)
                }
                ProfileViewModel.AWAIT_COMMON_DOC -> {
                    binding.showShortSnack(R.string.snack_generation_start)
                }
            }
        }
    }

    private fun setupProfileInfo(profile: ProfileUI) {
        with(binding) {
            profileImage.setData(getFullName(profile.firstName, profile.lastName), profile.avatar)
                .load()
            profileName.setTextOrHideField(
                getFullNameWithLineBreak(
                    profile.firstName,
                    profile.lastName
                )
            )
            profilePosition.setTextOrHideField(profile.position)
            profileUnit.setTextOrHideField(profile.unitTitle)
            profilePhone.setTextOrHideField(formatNumber(profile.phoneNumber))
            if (profilePhone.isVisible) {
                profilePhone.setOnClickListener { view ->
                    ClipboardUiUtils.copyPhoneNumber(
                        view,
                        requireNotNull(profile.phoneNumber)
                    )
                }
            }
            personalDataAdapter.setPersonalData(
                getListFromProfile(profile)
            )
            setCommonDoc(profile.commonDocument)
            showLoading(false)
        }
    }

    private fun setCommonDoc(commonDocument: CommonDocumentUI?) {
        with(binding) {
            if (commonDocument == null) {
                commonDocContainer.isGone = true
            } else {
                commonDocContainer.isVisible = true
                commonDocDate.text = getString(
                    R.string.date_pattern,
                    commonDocument.date,
                    commonDocument.time
                )
                commonDocBtn.setOnClickListener {
                    viewModel.openFileData(commonDocument.documentUri)
                }
            }
        }
    }

    private fun getListFromProfile(profile: ProfileUI): List<BaseListItem> {
        val dataList = mutableListOf<BaseListItem>()
        if (profile.medicalExam != null) {
            dataList.add(getHeaderFor(MED_EXAM))
            dataList.add(
                ListFileData(
                    getString(
                        R.string.med_exam_pattern,
                        profile.medicalExam.number,
                        profile.medicalExam.expirationDate
                    ),
                    profile.medicalExam.fileUri ?: ""
                )
            )
        }
        if (profile.trainings.isNotEmpty()) {
            dataList.add(getHeaderFor(TRAININGS))
            dataList.addAll(
                profile.trainings.map {
                    ListFileData(
                        getString(
                            R.string.training_pattern,
                            it.number,
                            it.expirationDate
                        ), it.fileUri ?: ""
                    )
                }
            )
        }
        if (profile.briefings.isNotEmpty()) {
            dataList.add(getHeaderFor(BRIEFINGS))
            dataList.addAll(
                profile.briefings.map {
                    ListSignedData(it.category, it.signed)
                }
            )
        }
        if (profile.protectiveEquipmentCard != null) {
            dataList.add(getHeaderFor(CARD))
            dataList.add(
                ListFileData(
                    profile.protectiveEquipmentCard.number,
                    profile.protectiveEquipmentCard.fileUri ?: ""
                )
            )
        }
        if (profile.tools.isNotEmpty()) {
            dataList.add(getHeaderFor(TOOLS))
            dataList.addAll(
                profile.tools.map {
                    ListTextData(it.tool)
                }
            )
        }
        return dataList
    }

    private fun getHeaderFor(type: String): Header {
        return Header(
            when (type) {
                MED_EXAM -> getString(R.string.med_exam)
                TRAININGS -> getString(R.string.trainings)
                BRIEFINGS -> getString(R.string.briefings)
                CARD -> getString(R.string.card)
                TOOLS -> getString(R.string.tools)
                else -> throw IllegalArgumentException()
            }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (!isLoading) {
                placeholder.root.hideAndShow(
                    content,
                    viewHiddenAction = { placeholder.root.stopShimmer() }
                )
            }
        }
    }

    companion object {

        fun createIntent(context: Context, id: Int): Intent {
            return Intent(context, ProfileActivity::class.java)
                .putExtra(EXTRA_USER_ID, id)
        }

        const val MED_EXAM = "med_exam"
        const val TRAININGS = "trainings"
        const val BRIEFINGS = "briefings"
        const val CARD = "card"
        const val TOOLS = "tools"

        const val EXTRA_USER_ID = "user_id"

        private const val SPACE_DP = 8
    }
}
