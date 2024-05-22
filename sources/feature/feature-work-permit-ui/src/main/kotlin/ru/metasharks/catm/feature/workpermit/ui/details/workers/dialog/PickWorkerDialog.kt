package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.core.ui.utils.setupSearch
import ru.metasharks.catm.feature.workpermit.ui.databinding.DialogPickWorkerBinding
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.recycler.WorkerAdapter
import ru.metasharks.catm.utils.argumentDelegate
import timber.log.Timber

@AndroidEntryPoint
class PickWorkerDialog : BottomSheetDialogFragment() {

    private val binding: DialogPickWorkerBinding by viewBinding(CreateMethod.INFLATE)
    private val initialPickedEmployees: List<WorkerListItemUi> by argumentDelegate(
        ARG_PICKED_WORKERS
    )

    private val excludedWorkersIds: List<String> by argumentDelegate(
        ARG_EXCLUDED_WORKERS_IDS
    )

    private val changePayload: ChangePayload by argumentDelegate(ARG_CHANGE_PAYLOAD)

    private lateinit var callback: Callback

    private val viewModel: PickWorkerViewModel by viewModels()

    private val pickItemAdapter: WorkerAdapter by lazy {
        WorkerAdapter(
            ::onAddButtonClicked,
            ::onChangeButtonClicked,
            ::onNearEndListener,
            changePayload.changeMode
        )
    }

    private fun onAddButtonClicked(workerUi: WorkerListItemUi) {
        viewModel.selectItem(workerUi)
        callback.onItemPick(workerUi, workerUi.isSelected)
    }

    private fun onChangeButtonClicked(workerUi: WorkerListItemUi) {
        val replacingWorkerInfo = requireNotNull(changePayload.replacingWorkerInfo)
        val newWorker = workerUi.copy(replacingWorkerId = replacingWorkerInfo.replacingWorkerId)
        callback.onItemChange(newWorker, replacingWorkerInfo)
        dismiss()
    }

    private fun onNearEndListener() {
        viewModel.load(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = requireListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            ru.metasharks.catm.core.ui.R.style.KeyboardBottomSheetDialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        setupUi()
        setupObservers()
        viewModel.setInitialItems(initialPickedEmployees)
        viewModel.setExcludedItems(excludedWorkersIds)
        viewModel.load(false)
    }

    private fun setupObservers() {
        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            pickItemAdapter.notifyItemChanged(item)
        }
        viewModel.adapterItems.observe(viewLifecycleOwner) { items ->
            pickItemAdapter.items = items
        }
    }

    private fun setupUi() {
        binding.itemsRecycler.apply {
            adapter = pickItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    interface Callback {

        fun onItemPick(item: WorkerListItemUi, isSelected: Boolean)

        fun onItemChange(
            item: WorkerListItemUi,
            replacingWorkerUserId: ChangePayload.ReplacingWorkerInfo
        )
    }

    private fun setupSearch() {
        binding.searchField
            .setupSearch()
            .subscribe(
                { message ->
                    viewModel.search(message)
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    @Parcelize
    class ChangePayload(
        val changeMode: Boolean,
        val replacingWorkerInfo: ReplacingWorkerInfo? = null
    ) : Parcelable {

        @Parcelize
        class ReplacingWorkerInfo(
            val replacingWorkerUserId: Long,
            val replacingWorkerId: Long?,
        ) : Parcelable
    }

    companion object {

        fun newInstance(
            pickedWorkers: List<WorkerListItemUi>,
            excludedWorkersIds: List<String> = emptyList(),
            changePayload: ChangePayload,
        ): PickWorkerDialog {
            val fragment = PickWorkerDialog()
            fragment.arguments = bundleOf(
                ARG_PICKED_WORKERS to pickedWorkers,
                ARG_EXCLUDED_WORKERS_IDS to excludedWorkersIds,
                ARG_CHANGE_PAYLOAD to changePayload,
            )
            return fragment
        }

        const val TAG = "PickWorkerDialog"

        private const val ARG_PICKED_WORKERS = "picked_workers"
        private const val ARG_EXCLUDED_WORKERS_IDS = "excluded_workers_ids"
        private const val ARG_CHANGE_PAYLOAD = "change_payload"
    }
}
