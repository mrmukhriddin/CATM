package ru.metasharks.catm.feature.profile.ui

import ru.metasharks.catm.core.ui.bottom.PageInfo

object PagesInfo {

    val mainScreenPageInfo = PageInfo(
        menuId = R.id.page_main,
        titleRes = R.string.tab_label_main,
        iconRes = R.drawable.ic_qr_code,
    )

    val briefingsScreenPageInfo = PageInfo(
        menuId = R.id.page_briefings,
        titleRes = R.string.tab_label_briefings,
        iconRes = R.drawable.ic_briefings,
    )

    val trainingsScreenPageInfo = PageInfo(
        menuId = R.id.page_trainings,
        titleRes = R.string.tab_label_trainings,
        iconRes = R.drawable.ic_trainings,
    )

    val documentsScreenPageInfo = PageInfo(
        menuId = R.id.page_documents,
        titleRes = R.string.tab_label_documents,
        iconRes = R.drawable.ic_documents,
    )

    val feedbackScreenPageInfo = PageInfo(
        menuId = R.id.page_feedback,
        titleRes = R.string.tab_label_feedback,
        iconRes = R.drawable.ic_feedback,
    )
}
