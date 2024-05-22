package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import ru.metasharks.catm.feature.briefings.entities.MainBriefingData
import javax.inject.Inject

fun interface GetMainBriefingDataUseCase {

    operator fun invoke(): Single<MainBriefingData>
}

internal class GetMainBriefingDataUseCaseImpl @Inject constructor(
    private val getBriefingCategoriesUseCase: GetBriefingCategoriesUseCase,
    private val getBriefingsUseCase: GetBriefingsUseCase,
    private val getBriefingTypesUseCase: GetBriefingTypesUseCase,
) : GetMainBriefingDataUseCase {

    override fun invoke(): Single<MainBriefingData> = Singles.zip(
        getBriefingCategoriesUseCase(),
        getBriefingTypesUseCase(),
        getBriefingsUseCase(fromCache = false),
    ).map { (categories, types, briefings) -> MainBriefingData(categories, types, briefings) }
}
