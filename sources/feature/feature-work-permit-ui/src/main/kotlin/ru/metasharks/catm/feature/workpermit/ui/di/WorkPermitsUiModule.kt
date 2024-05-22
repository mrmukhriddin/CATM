package ru.metasharks.catm.feature.workpermit.ui.di

import android.content.Context
import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.CreateDailyPermitScreen
import ru.metasharks.catm.core.navigation.screens.CreateGasAirAnalysisScreen
import ru.metasharks.catm.core.navigation.screens.DailyPermitsScreen
import ru.metasharks.catm.core.navigation.screens.EndDailyPermitScreen
import ru.metasharks.catm.core.navigation.screens.ExtendWorkPermitScreen
import ru.metasharks.catm.core.navigation.screens.GasAirAnalyzesScreen
import ru.metasharks.catm.core.navigation.screens.RiskFactorsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsAdditionalDocumentsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsSignersScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitDetailsWorkersScreen
import ru.metasharks.catm.core.navigation.screens.WorkPermitsScreen
import ru.metasharks.catm.core.navigation.screens.result.ResultScreen
import ru.metasharks.catm.feature.workpermit.ui.details.WorkPermitDetailsActivity
import ru.metasharks.catm.feature.workpermit.ui.details.adddocs.AdditionalDocumentsActivity
import ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.DailyPermitsActivity
import ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.create.CreateDailyPermitActivity
import ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.end.EndDailyPermitActivity
import ru.metasharks.catm.feature.workpermit.ui.details.extend.ExtendWorkPermitActivity
import ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.GasAirAnalyzesActivity
import ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.create.CreateGasAirAnalysisActivity
import ru.metasharks.catm.feature.workpermit.ui.details.riskfactors.RiskFactorsActivity
import ru.metasharks.catm.feature.workpermit.ui.details.signers.SignersActivity
import ru.metasharks.catm.feature.workpermit.ui.details.workers.WorkersActivity
import ru.metasharks.catm.feature.workpermit.ui.list.WorkPermitsActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class WorkPermitsUiModule private constructor() {

    companion object {

        @Provides
        fun provideWorkPermitDetailsWorkersScreen(): WorkPermitDetailsWorkersScreen =
            WorkPermitDetailsWorkersScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = WorkPermitDetailsWorkersScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return WorkersActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideWorkPermitDetailsSignersScreen(): WorkPermitDetailsSignersScreen =
            WorkPermitDetailsSignersScreen { workPermitId ->
                ActivityScreen { context ->
                    SignersActivity.createIntent(context, workPermitId)
                }
            }

        @Provides
        fun provideWorkPermitDetailsAdditionalDocumentsScreen(): WorkPermitDetailsAdditionalDocumentsScreen =
            WorkPermitDetailsAdditionalDocumentsScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = WorkPermitDetailsAdditionalDocumentsScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return AdditionalDocumentsActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideWorkPermitDetailsScreen(): WorkPermitDetailsScreen =
            WorkPermitDetailsScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = WorkPermitDetailsScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return WorkPermitDetailsActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideWorkPermitsScreen(): WorkPermitsScreen = WorkPermitsScreen {
            ActivityScreen { context ->
                WorkPermitsActivity.createIntent(context)
            }
        }

        @Provides
        fun provideRiskFactorsScreen(): RiskFactorsScreen = RiskFactorsScreen {
            ActivityScreen { context ->
                RiskFactorsActivity.createIntent(context)
            }
        }

        @Provides
        fun provideGasAirAnalyzesScreen(): GasAirAnalyzesScreen =
            GasAirAnalyzesScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = GasAirAnalyzesScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return GasAirAnalyzesActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideCreateGasAirAnalysisScreen(): CreateGasAirAnalysisScreen =
            CreateGasAirAnalysisScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = CreateGasAirAnalysisScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return CreateGasAirAnalysisActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideExtendWorkPermitScreen(): ExtendWorkPermitScreen =
            ExtendWorkPermitScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = ExtendWorkPermitScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return ExtendWorkPermitActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideDailyPermitsScreen(): DailyPermitsScreen =
            DailyPermitsScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = DailyPermitsScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return DailyPermitsActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideCreateDailyPermitScreen(): CreateDailyPermitScreen =
            CreateDailyPermitScreen { workPermitId ->
                object : ResultScreen {

                    override val resultKey: String = CreateDailyPermitScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return CreateDailyPermitActivity.createIntent(context, workPermitId)
                    }
                }
            }

        @Provides
        fun provideEndDailyPermitScreen(): EndDailyPermitScreen =
            EndDailyPermitScreen { workPermitId, dailyPermitId ->
                object : ResultScreen {

                    override val resultKey: String = EndDailyPermitScreen.KEY

                    override fun createIntent(context: Context): Intent {
                        return EndDailyPermitActivity.createIntent(
                            context,
                            workPermitId,
                            dailyPermitId
                        )
                    }
                }
            }
    }
}
