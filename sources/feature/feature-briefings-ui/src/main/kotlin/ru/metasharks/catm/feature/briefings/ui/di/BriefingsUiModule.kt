package ru.metasharks.catm.feature.briefings.ui.di

import android.content.Context
import android.content.Intent
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.BriefingDetailsScreen
import ru.metasharks.catm.core.navigation.screens.fragments.BriefingsScreen
import ru.metasharks.catm.core.navigation.screens.result.ResultScreen
import ru.metasharks.catm.feature.briefings.ui.main.BriefingMainFragment
import ru.metasharks.catm.feature.briefings.ui.main.briefings.BriefingsListFragment
import ru.metasharks.catm.feature.briefings.ui.main.categories.BriefingCategoriesFragment
import ru.metasharks.catm.feature.briefings.ui.main.details.BriefingDetailsActivity
import ru.metasharks.catm.feature.briefings.ui.main.quiz.BriefingQuizActivity
import ru.metasharks.catm.feature.briefings.ui.main.types.BriefingTypesFragment
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingCategoriesScreen
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingQuizScreen
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingTypesScreen
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingsListScreen

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BriefingsUiModule private constructor() {

    companion object {

        @Provides
        fun provideBriefingsScreen(): BriefingsScreen = BriefingsScreen {
            FragmentScreen { BriefingMainFragment() }
        }

        // inner nav:
        @Provides
        fun provideBriefingCategoriesScreen(): BriefingCategoriesScreen = BriefingCategoriesScreen {
            FragmentScreen { BriefingCategoriesFragment() }
        }

        @Provides
        fun provideBriefingTypesScreen(): BriefingTypesScreen = BriefingTypesScreen { categoryId ->
            FragmentScreen { BriefingTypesFragment.newInstance(categoryId) }
        }

        @Provides
        fun provideBriefingsListScreen(): BriefingsListScreen =
            BriefingsListScreen { categoryId, typeId ->
                FragmentScreen { BriefingsListFragment.newInstance(categoryId, typeId) }
            }

        @Provides
        fun provideBriefingDetailsScreen(): BriefingDetailsScreen =
            BriefingDetailsScreen { briefingId ->

                object : ResultScreen {

                    override val resultKey: String = BriefingDetailsScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return BriefingDetailsActivity.createIntent(
                            context,
                            briefingId
                        )
                    }
                }
            }

        @Provides
        fun provideBriefingQuizScreen(): BriefingQuizScreen = BriefingQuizScreen { briefingId ->
            object : ResultScreen {

                override val resultKey: String
                    get() = BriefingQuizScreen.KEY

                override fun createIntent(context: Context): Intent {
                    return BriefingQuizActivity.createIntent(context, briefingId)
                }
            }
        }
    }
}
