package ru.metasharks.catm.feature.briefings.ui.entities

data class BriefingDetailsUi(
    val text: String,
    val brId: Int,
    val categoryId: Int,
    val typeId: Int,
    val content: String,
    val fileData: FileData?,
    val quizId: Long?,
    val bottomActions: BottomActions,
) {
    class FileData(
        val url: String,
        val sizeAndExtension: String,
        val name: String,
    )

    class BottomActions(
        val bottomResultsText: String? = null, // слева галочка- справа текст
        val bottomQuizButtonVisible: Boolean = false, // кнопка для прохождения теста

        val bottomSignButtonVisible: Boolean = false, // показать кнопку "подписать"
        val bottomSignButtonEnabled: Boolean = false, // активна ли кнопка "подписать"?
    )
}
