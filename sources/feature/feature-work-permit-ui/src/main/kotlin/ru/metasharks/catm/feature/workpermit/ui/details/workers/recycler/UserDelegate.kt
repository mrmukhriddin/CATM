package ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemUserBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.UserListItemUi
import ru.metasharks.catm.utils.layoutInflater

class UserDelegate : BaseListItemDelegate<UserListItemUi, UserDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemUserBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(item: UserListItemUi, holder: ViewHolder, payloads: List<Any?>) {
        with(holder.binding) {
            with(profileInfo) {
                profileName.text = getFullNameWithLineBreak(item.name, item.surname)
                if (item.position == null) {
                    profilePosition.isGone = true
                    profilePosition.text = ""
                } else {
                    profilePosition.isVisible = true
                    profilePosition.text = item.position
                }
                profileImageContainer.setData(getFullName(item.name, item.surname), item.avatar)
                    .load()
            }
            isReadyIndicator.isEnabled = item.isReady ?: false
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is UserListItemUi

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}
