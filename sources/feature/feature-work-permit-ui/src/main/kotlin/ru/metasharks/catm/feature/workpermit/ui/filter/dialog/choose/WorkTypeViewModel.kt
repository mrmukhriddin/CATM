package ru.metasharks.catm.feature.workpermit.ui.filter.dialog.choose

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.dialog.pick.PickItemViewModel
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.searchmanager.SearchListManager
import ru.metasharks.catm.core.ui.searchmanager.SelectableSearchListManager
import ru.metasharks.catm.feature.workpermit.ui.mapper.WorkTypeMapper
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkTypesUseCase
import javax.inject.Inject

@HiltViewModel
class WorkTypeViewModel @Inject constructor(
    private val getWorkTypesUseCase: GetWorkTypesUseCase,
    private val workTypeMapper: WorkTypeMapper
) : PickItemViewModel<SelectableBaseListItem, PickItemDialog.ItemUi>() {

    override val currentItems: MutableLiveData<List<SelectableBaseListItem>> = MutableLiveData()

    override val dataSource = object : SearchListManager.DataSource<PickItemDialog.ItemUi> {

        override fun load(nextPageUrl: String?): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
            return getWorkTypesUseCase().map {
                it.map(workTypeMapper::mapWorkTypeToPickItem) to null
            }
        }

        override fun search(
            searchQuery: String,
            nextPageUrl: String?
        ): Single<Pair<List<PickItemDialog.ItemUi>, String?>> {
            return Single.fromCallable {
                (currentItems.value ?: emptyList())
                    .filterIsInstance<PickItemDialog.ItemUi>()
                    .filter { it.value.contains(searchQuery.trim(), ignoreCase = true) } to null
            }
        }
    }

    override val error: MutableLiveData<Throwable> = MutableLiveData()

    override val callback: SelectableSearchListManager.Callback<SelectableBaseListItem> =
        object : SelectableSearchListManager.Callback<SelectableBaseListItem> {

            override fun onLoading(start: Boolean) = Unit

            override fun onItemSelected(item: SelectableBaseListItem, isSelected: Boolean) {
                _selectedItem.value = item
            }
        }

    override fun search(query: String) {
        searchManager.search(query)
    }

    override fun load(newPage: Boolean) {
        searchManager.load(newPage)
    }
}
