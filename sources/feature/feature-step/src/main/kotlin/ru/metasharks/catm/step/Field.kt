package ru.metasharks.catm.step

import android.view.View
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.utils.strings.StringResWrapper

/**
 * T - тип изначальных данных. Изначальные данные - это данные, которые могут быть проставлены
 * этому полю при инициализации
 */
sealed class Field<T>(
    val tag: String,
    val input: BaseInput<T>?
) {

    abstract val type: PatternDataType

    class Text(tag: String, input: Input) : Field<String>(tag, input) {

        override val type: PatternDataType = PatternDataType.TEXT

        class Input(
            val label: StringResWrapper,
            val hint: StringResWrapper,
            override val initialValue: String? = null,
            val oneLine: Boolean = true,
            val isEditable: Boolean = true,
            val clarification: StringResWrapper? = null,
        ) : BaseInput<String>(initialValue)
    }

    class DoubleText(tag: String, input: Input) : Field<Pair<String, String>>(tag, input) {

        override val type: PatternDataType = PatternDataType.DOUBLE_TEXT

        class Input(
            val label: StringResWrapper,
            val hintFirst: StringResWrapper,
            val hintSecond: StringResWrapper,
            override val initialValue: Pair<String, String>? = null,
            val isEditable: Boolean = true,
            val clarification: String? = null,
        ) : BaseInput<Pair<String, String>>(initialValue)
    }

    class Pick(tag: String, input: Input) : Field<Pick.InitialValue>(tag, input) {

        override val type: PatternDataType = PatternDataType.PICK

        data class InitialValue(
            val item: PickItemDialog.ItemUi
        )

        class Input(
            val label: StringResWrapper,
            val hint: StringResWrapper,
            val onSelectCallback: (View) -> Unit,
            val isEditable: Boolean = true,
            initialValue: InitialValue? = null
        ) : BaseInput<InitialValue>(initialValue)
    }

    class Check(tag: String, input: Input) : Field<Boolean>(tag, input) {

        override val type: PatternDataType = PatternDataType.CHECK

        class Input(
            val description: StringResWrapper,
            val valueId: Long,
            override val initialValue: Boolean? = null
        ) : BaseInput<Boolean>(initialValue)
    }

    class DateAndTime(tag: String, input: Input) : Field<Pair<String, String>>(tag, input) {

        override val type: PatternDataType = PatternDataType.DATE_AND_TIME

        class Input(
            val label: StringResWrapper,
            override val initialValue: Pair<String, String>? = null,
            val canEditDate: Boolean = true
        ) : BaseInput<Pair<String, String>>(initialValue)
    }

    class Date(tag: String, input: Input) : Field<String>(tag, input) {

        override val type: PatternDataType = PatternDataType.DATE

        class Input(
            val label: StringResWrapper,
            override val initialValue: String? = null,
            val canEditDate: Boolean = true
        ) : BaseInput<String>(initialValue)
    }

    class Header(tag: String, input: Input) : Field<String>(tag, input) {

        override val type: PatternDataType = PatternDataType.HEADER

        class Input(
            val text: String,
        ) : BaseInput<String>(text)
    }

    class Radio(tag: String, input: Input) : Field<String>(tag, input) {

        override val type: PatternDataType = PatternDataType.RADIO

        class Input(
            val label: StringResWrapper,
            val radioValues: List<Pair<StringResWrapper, String>>,
            override val initialValue: String? = null,
        ) : BaseInput<String>(initialValue)
    }

    class Progress : Field<Any>(TAG, null) {

        override val type: PatternDataType = PatternDataType.PROGRESS

        companion object {
            const val TAG = "progress_tag"
        }
    }
}
