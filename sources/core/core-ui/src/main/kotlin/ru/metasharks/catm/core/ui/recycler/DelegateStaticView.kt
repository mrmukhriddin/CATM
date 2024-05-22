package ru.metasharks.catm.core.ui.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

open class DelegateStaticView<ItemType : BaseListItemType, BaseListItemType>(
    private val itemClass: Class<ItemType>,
    @LayoutRes private val layoutRes: Int
) : AbsListItemAdapterDelegate<ItemType, BaseListItemType, DelegateStaticView.ViewHolder>() {

    override fun isForViewType(
        item: BaseListItemType,
        items: List<BaseListItemType>,
        position: Int
    ): Boolean = itemClass.isInstance(item)

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(layoutRes, parent, false))
    }

    override fun onBindViewHolder(item: ItemType, holder: ViewHolder, payloads: List<Any>) = Unit

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
