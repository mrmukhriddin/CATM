package ru.metasharks.catm.core.ui.bottom

import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.Cicerone
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.metasharks.catm.core.navigation.bottom.BottomNavigator
import ru.metasharks.catm.core.navigation.bottom.BottomRouter

class BottomNavigationController(
    private val supportFragmentManager: FragmentManager,
    private val bottomNavigationView: BottomNavigationView,
    private val container: FrameLayout,
    private val firstItemId: Int? = null,
    private val onPageSelectedListener: (coreScreenPage: CoreScreenPage, screenId: Int) -> Unit = { _, _ -> },
) {

    val currentScreenId: Int
        get() = bottomNavigationView.selectedItemId

    private var pages: List<CoreScreenPage> = emptyList()

    private var initialItemId: Int = -1

    private val cicerone = Cicerone.create(BottomRouter()).apply {
        getNavigatorHolder().setNavigator(BottomNavigator(container, supportFragmentManager))
    }

    private val bottomRouter: BottomRouter
        get() = cicerone.router

    fun setPages(pages: List<CoreScreenPage>) {
        bottomNavigationView.menu.clear()
        pages.forEach {
            processMenuItemForPage(
                bottomNavigationView.menu.add(
                    Menu.NONE, // group id
                    it.info.menuId, // menu id
                    Menu.NONE, // order
                    it.info.titleRes, // title res id
                ),
                it
            )
        }
        this.pages = pages
    }

    private fun processMenuItemForPage(menuItem: MenuItem, page: CoreScreenPage) {
        val pageInfo = page.info
        menuItem.setIcon(pageInfo.iconRes)
    }

    fun isOnInitialScreen(): Boolean =
        bottomNavigationView.selectedItemId == initialItemId

    fun returnToInitialScreen(): Boolean {
        if (bottomNavigationView.selectedItemId == initialItemId) {
            return false
        }
        bottomNavigationView.selectedItemId = initialItemId
        return true
    }

    fun init() {
        if (pages.isEmpty()) return
        with(bottomNavigationView) {
            setOnItemSelectedListener { menuItem ->
                openScreen(menuItem.itemId)
                onPageSelectedListener(
                    pages.first { it.info.menuId == menuItem.itemId },
                    menuItem.itemId
                )
                return@setOnItemSelectedListener true
            }
            initialItemId = firstItemId ?: pages.first().info.menuId
            selectedItemId = initialItemId
        }
    }

    private fun openScreen(itemId: Int) {
        val page = pages.first { it.info.menuId == itemId }
        bottomRouter.switchScreen(page.screenFactory())
    }

    fun setEnabledMenuItem(menuItemId: Int, enabled: Boolean) {
        val item = bottomNavigationView.menu.findItem(menuItemId)
        if (item.isChecked) {
            bottomNavigationView.selectedItemId = initialItemId
        }
        item.isEnabled = enabled
    }
}
