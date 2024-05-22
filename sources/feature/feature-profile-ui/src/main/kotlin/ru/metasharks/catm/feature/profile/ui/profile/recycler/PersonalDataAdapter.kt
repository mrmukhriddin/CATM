package ru.metasharks.catm.feature.profile.ui.profile.recycler

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListFileData

class PersonalDataAdapter(
    onFileClickListener: (fileData: ListFileData) -> Unit,
) : ListDelegationAdapter<List<BaseListItem>>() {

    init {
        delegatesManager.addDelegate(DelegateHeader())
            .addDelegate(DelegateSignedData())
            .addDelegate(DelegateFileData(onFileClickListener))
            .addDelegate(DelegateTextData())
    }

    fun setPersonalData(items: List<BaseListItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
