package ru.metasharks.catm.feature.briefings.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.ToolbarCallback
import ru.metasharks.catm.core.ui.fragment.requireListener
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import timber.log.Timber

@AndroidEntryPoint
internal class BriefingMainFragment : Fragment(R.layout.fragment_briefings_main),
    BriefingMainCallbacks {

    private val viewModel: BriefingMainViewModel by viewModels()
    private lateinit var toolbarCallback: ToolbarCallback

    private val briefingsNavigator: Navigator by lazy {
        AppNavigator(requireActivity(), R.id.briefings_main, childFragmentManager)
    }

    private val cicerone: Cicerone<Router> by lazy {
        Cicerone.create().apply {
            getNavigatorHolder().setNavigator(briefingsNavigator)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolbarCallback = requireListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.innerRouter = cicerone.router
        if (childFragmentManager.backStackEntryCount == 0) {
            viewModel.openBriefingCategoriesScreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }

    override fun getMainViewModel(): BriefingMainViewModel = viewModel

    override fun openBriefingTypes(categoryId: Int) {
        viewModel.openBriefingTypesScreen(categoryId)
        childFragmentManager.updateToolbarBackListener(addScreen = true)
    }

    override fun openBriefingsList(categoryId: Int, typeId: Int) {
        viewModel.openBriefingsListScreen(categoryId, typeId)
        childFragmentManager.updateToolbarBackListener(addScreen = true)
    }

    override fun openBriefingDetails(briefing: BriefingUi) {
        viewModel.openBriefingDetails(briefing)
    }

    /**
     * @param addScreen: true если добавляем экран в fragmentManager; false = если убираем (с помощью popBackStack)
     */
    private fun FragmentManager.updateToolbarBackListener(addScreen: Boolean) {
        val level = if (addScreen) backStackEntryCount + 1 else backStackEntryCount - 1
        Timber.tag("BriefingMainFragment")
            .d("updateToolbarBackListener level: $level; backStackEntryCount: $backStackEntryCount")
        if (level == 1) {
            toolbarCallback.showBack(false, null)
        } else {
            toolbarCallback.showBack(true) {
                Timber.tag("BriefingMainFragment")
                    .d("popBackStack() level: $level; backStackEntryCount: $backStackEntryCount")
                popBackStack()
                updateToolbarBackListener(addScreen = false)
            }
        }
    }
}
