package ru.metasharks.catm.core.ui.dialog.pick

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.ui.databinding.DialogCreateWpPickItemBinding
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.utils.setupSearch
import ru.metasharks.catm.utils.argumentDelegate
import timber.log.Timber

@AndroidEntryPoint
class PickItemDialog : BottomSheetDialogFragment() {

    private val binding: DialogCreateWpPickItemBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModelClass:
            Class<PickItemViewModel<SelectableBaseListItem, ItemUi>>
            by argumentDelegate(ARG_VIEW_MODEL_CLASS)

    private val titleString: String? by argumentDelegate(ARG_TITLE)
    private val initialItem: ItemUi? by argumentDelegate(ARG_INITIAL_ITEM_ID)
    private val dismissOnPick: Boolean? by argumentDelegate(ARG_DISMISS_ON_PICK)

    private lateinit var callback: Callback

    private val viewModel: PickItemViewModel<SelectableBaseListItem, ItemUi> by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    private val pickItemAdapter: PickItemAdapter by lazy {
        PickItemAdapter(
            this::onItemClick,
            this::onNearEndListener
        )
    }

    private fun onItemClick(itemUi: ItemUi) {
        viewModel.select(itemUi)
        callback.onItemPicked(tag ?: TAG, viewModel.pickedItem)
        if (dismissOnPick == true) {
            dismiss()
        }
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
        viewModel.load(false, initialItem)
    }

    private fun setupObservers() {
        viewModel.currentItems.observe(viewLifecycleOwner) { items ->
            pickItemAdapter.items = items
        }
        viewModel.selectedItem.observe(viewLifecycleOwner) { selectedItem ->
            pickItemAdapter.selectItem(selectedItem)
        }
    }

    private fun setupUi() {
        if (titleString != null) {
            binding.title.text = titleString
        }
        binding.itemsRecycler.apply {
            adapter = pickItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
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

    companion object {

        const val ARG_TITLE = "title"
        const val ARG_VIEW_MODEL_CLASS = "view_model_class"
        const val ARG_INITIAL_ITEM_ID = "initial_item_id"
        const val ARG_DISMISS_ON_PICK = "dismiss_on_pick"
        const val TAG = "PickItemDialog"
    }

    class Builder(private val context: Context) {

        private var args = Bundle()

        fun build(): PickItemDialog {
            return PickItemDialog().apply {
                arguments = args
            }
        }

        fun setTitle(@StringRes titleRes: Int): Builder {
            return setTitle(context.getString(titleRes))
        }

        fun setTitle(title: String): Builder {
            args.putString(ARG_TITLE, title)
            return this
        }

        fun <T : PickItemViewModel<SelectableBaseListItem, out SelectableBaseListItem>> setViewModelClass(
            clazz: Class<T>
        ): Builder {
            args.putSerializable(ARG_VIEW_MODEL_CLASS, clazz)
            return this
        }

        fun setInitialItem(item: ItemUi?): Builder {
            if (item != null) {
                args.putParcelable(ARG_INITIAL_ITEM_ID, item)
            }
            return this
        }

        fun setDismissOnPick(dismiss: Boolean): Builder {
            args.putBoolean(ARG_DISMISS_ON_PICK, dismiss)
            return this
        }
    }

    @Parcelize
    data class ItemUi(
        val entityId: Long,
        val value: String,
        override var isSelected: Boolean = false,
    ) : SelectableBaseListItem, Parcelable {

        override val id: String
            get() = entityId.toString()
    }

    interface Callback {

        fun onItemPicked(tag: String, item: ItemUi?)
    }
}
