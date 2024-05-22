package ru.metasharks.catm.feature.workpermit.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemMenuOptionBinding
import ru.metasharks.catm.feature.workpermit.ui.utils.PopupUtils
import ru.metasharks.catm.utils.extendClickableZone
import ru.metasharks.catm.utils.layoutInflater

class MenuManager(
    private val context: Context,
    private val menuContainer: ViewGroup
) {

    private var currentPopupWindow: PopupWindow? = null

    private lateinit var menuEntry: MenuEntry

    private val layoutInflater: LayoutInflater
        get() = context.layoutInflater

    fun init() {
        menuEntry.options.forEach {
            val view = createMenuOption(it)
            menuContainer.addView(view)
        }
    }

    fun setMenuEntry(entry: MenuEntry) {
        menuEntry = entry
    }

    private fun createMenuOption(option: MenuOption): View {
        val binding = ItemMenuOptionBinding.inflate(layoutInflater)
        with(binding) {
            root.setOnClickListener {
                menuEntry.onMenuOptionClick(option.tag)
            }
            root.tag = option.tag
            menuText.text = option.text
            if (option.error != null) {
                errorIndicator.isVisible = true
                errorIndicator.extendClickableZone(EXTRA_PAD, EXTRA_PAD, EXTRA_PAD, EXTRA_PAD)
                errorIndicator.setOnClickListener { anchor ->
                    currentPopupWindow?.dismiss()
                    currentPopupWindow = PopupUtils.showPopup(anchor, option.error)
                }
            }
        }
        return when (option) {
            is MenuOption.Count -> {
                with(binding) {
                    count.isVisible = true
                    if (option.overall == null) {
                        count.text = option.count.toString()
                    } else {
                        count.text = context.getString(
                            R.string.pattern_number_of_overall,
                            option.count,
                            option.overall
                        )
                    }
                }
                binding.root
            }
            is MenuOption.Check -> {
                with(binding) {
                    check.isVisible = true
                    check.isEnabled = option.isChecked
                }
                binding.root
            }
        }
    }

    fun onDestroy() {
        currentPopupWindow?.dismiss()
        currentPopupWindow = null
    }

    fun updateMenu(tag: String, action: (ItemMenuOptionBinding) -> Unit) {
        val view = menuContainer.findViewWithTag<View>(tag) ?: return
        val binding = ItemMenuOptionBinding.bind(view)
        action(binding)
    }

    fun removeAllViews() {
        menuContainer.removeAllViews()
    }

    companion object {

        private const val EXTRA_PAD = 20
    }
}

sealed class MenuOption(val text: String, val tag: String, val error: String? = null) {

    class Check(tag: String, text: String, val isChecked: Boolean) : MenuOption(text, tag)

    class Count(
        tag: String,
        text: String,
        val count: Int,
        val overall: Int? = null,
        error: String? = null
    ) : MenuOption(text, tag, error)
}
