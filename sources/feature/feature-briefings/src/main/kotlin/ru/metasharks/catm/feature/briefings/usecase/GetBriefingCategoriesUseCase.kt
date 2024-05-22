package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.entities.BriefingCategoryX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface GetBriefingCategoriesUseCase {

    operator fun invoke(): Single<List<BriefingCategoryX>>
}

internal class GetBriefingCategoriesUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
) : GetBriefingCategoriesUseCase {

    override fun invoke(): Single<List<BriefingCategoryX>> = api.getCategories()
}
