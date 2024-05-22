package ru.metasharks.catm.step

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.step.types.check.CheckFactory
import ru.metasharks.catm.step.types.check.CheckManager
import ru.metasharks.catm.step.types.date.DateFactory
import ru.metasharks.catm.step.types.date.DateManager
import ru.metasharks.catm.step.types.dateandtime.DateAndTimeFactory
import ru.metasharks.catm.step.types.dateandtime.DateAndTimeManager
import ru.metasharks.catm.step.types.doubletext.DoubleTextFactory
import ru.metasharks.catm.step.types.doubletext.DoubleTextManager
import ru.metasharks.catm.step.types.header.HeaderFactory
import ru.metasharks.catm.step.types.pick.PickFactory
import ru.metasharks.catm.step.types.pick.PickManager
import ru.metasharks.catm.step.types.progress.ProgressFactory
import ru.metasharks.catm.step.types.radio.RadioFactory
import ru.metasharks.catm.step.types.radio.RadioManager
import ru.metasharks.catm.step.types.text.TextFactory
import ru.metasharks.catm.step.types.text.TextManager
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.step.validator.Validator
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.setDefiniteMargin

class StepPatternLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private fun getValuesFromAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.StepPatternLayout
            )
        val initialLoading = typedArray.getBoolean(
            R.styleable.StepPatternLayout_initialLoading,
            false
        )
        if (initialLoading) {
            inflateOf(Field.Progress())
        }
        typedArray.recycle()
    }

    private val validators: MutableList<Validator<StepManager<*, *, *>>> = mutableListOf()

    @SuppressWarnings("unchecked")
    private val factories: Map<PatternDataType, StepFactory<*, Any?>> = mapOf(
        PatternDataType.TEXT to (TextFactory(this) as StepFactory<*, Any?>),
        PatternDataType.DOUBLE_TEXT to (DoubleTextFactory(this) as StepFactory<*, Any?>),
        PatternDataType.DATE_AND_TIME to (DateAndTimeFactory(this) as StepFactory<*, Any?>),
        PatternDataType.DATE to (DateFactory(this) as StepFactory<*, Any?>),
        PatternDataType.RADIO to (RadioFactory(this) as StepFactory<*, Any?>),
        PatternDataType.PICK to (PickFactory(this) as StepFactory<*, Any?>),
        PatternDataType.CHECK to (CheckFactory(this) as StepFactory<*, Any?>),
        PatternDataType.HEADER to (HeaderFactory(this) as StepFactory<*, Any?>),
        PatternDataType.PROGRESS to (ProgressFactory(this) as StepFactory<*, Any?>),
    )

    private val managers: Map<PatternDataType, StepManager<*, *, *>> = mapOf(
        PatternDataType.TEXT to TextManager(),
        PatternDataType.DOUBLE_TEXT to DoubleTextManager(),
        PatternDataType.DATE_AND_TIME to DateAndTimeManager(),
        PatternDataType.DATE to DateManager(),
        PatternDataType.RADIO to RadioManager(),
        PatternDataType.PICK to PickManager(),
        PatternDataType.CHECK to CheckManager(),
    )

    private val statusListener by lazy {
        StatusListener { mOnFormFilled }
    }

    private var mOnFormFilled: ((Boolean) -> Unit)? = null
    private var mOnValidationFailed: ((warning: String?) -> Unit)? = null
    private val tags: MutableMap<String, PatternDataType> = mutableMapOf()

    init {
        getValuesFromAttrs(attrs)
        orientation = VERTICAL
    }

    fun setOnFormFilledListener(onFormFilled: (Boolean) -> Unit) {
        mOnFormFilled = onFormFilled
    }

    fun clean() {
        removeAllViews()
    }

    fun inflateOf(
        vararg items: Field<out Any>,
        setNecessaryItems: Boolean = false
    ) {
        inflateOf(items.toList(), setNecessaryItems)
    }

    fun inflateOf(
        items: List<Field<out Any>>,
        setNecessaryItems: Boolean = false
    ) {
        inflateItems(
            items.map { input ->
                CreateItemData(itemType = input.type, tag = input.tag, prompt = input.input)
            }, setNecessaryItems
        )
    }

    fun inflateItems(itemPatternsIdsList: List<CreateItemData>, setNecessaryItems: Boolean) {
        clean()
        if (setNecessaryItems) {
            setNecessaryTags(itemPatternsIdsList.map { it.tag }.toSet())
        }
        itemPatternsIdsList.forEachIndexed { index, data ->
            val view = viewFactory(data)
            managers[data.itemType]?.signToStatusListener(view, data.tag, statusListener)
            if (index != 0) {
                view.setDefiniteMargin(top = context.dpToPx(DISTANCE_BETWEEN_FIELDS))
            }
            view.tag = data.tag
            addView(view)
            tags[data.tag] = data.itemType
        }
    }

    fun setNecessaryTags(tags: Set<String>) {
        statusListener.setNecessaryTags(tags)
    }

    fun setType(type: Int) {
        statusListener.type = type
    }

    fun addValidators(vararg validators: Validator<out StepManager<*, *, *>>) {
        for (each in validators) {
            addValidator(each)
        }
    }

    fun addValidator(validator: Validator<out StepManager<*, *, *>>) {
        validators.add(validator as Validator<StepManager<*, *, *>>)
    }

    private fun viewFactory(createItemData: CreateItemData): View {
        val factory = factories.getValue(createItemData.itemType)
        return factory.createView(createItemData.prompt)
    }

    fun gatherData(): MutableMap<String, Any?> {
        val listOfGatheredData = mutableMapOf<String, Any?>()
        tags.keys.forEach { itemTag ->
            listOfGatheredData[itemTag] = gatherDataFromField(itemTag)
        }
        return listOfGatheredData
    }

    fun gatherDataFromField(tag: String): Any? {
        val type = tags.getValue(tag)
        return managers[type]?.gatherDataFromView(findViewWithTag(tag))
    }

    fun validate(): Validator.Result {
        for (validator in validators) {
            val validationResult = validator.validate(
                managerGetter = { requireNotNull(managers[it]) },
                childGetter = { findViewWithTag(it) }
            )
            if (!validationResult.isValid) {
                return validationResult
            }
        }
        return Validator.Result(true, null)
    }

    fun <T> gatherNonNullData(prefix: String? = null): MutableMap<String, T> {
        val listOfGatheredData = mutableMapOf<String, T>()
        for (itemTag in tags.keys) {
            if (prefix != null && !itemTag.startsWith(prefix)) {
                continue
            }
            val type = tags.getValue(itemTag)
            managers[type]?.let { manager ->
                val data = manager.gatherDataFromView(findViewWithTag(itemTag))
                if (data != null) {
                    listOfGatheredData[itemTag] = data as T
                }
            }
        }
        return listOfGatheredData
    }

    fun gatherRestoreData(): MutableMap<String, Any?> {
        val listOfGatheredData = mutableMapOf<String, Any?>()
        for (itemTag in tags.keys) {
            val type = tags.getValue(itemTag)
            managers[type]?.let { manager ->
                val data = manager.gatherRestoreDataFromView(findViewWithTag(itemTag))
                listOfGatheredData[itemTag] = data
            }
        }
        return listOfGatheredData
    }

    fun gatherRestoreDataFromField(tag: String): Any? {
        val type = tags.getValue(tag)
        return managers[type]?.gatherRestoreDataFromView(findViewWithTag(tag))
    }

    fun setValueForFieldWithTag(tag: String, item: Any?) {
        val type = tags.getValue(tag)
        val manager = managers.getValue(type)
        manager.setValue(findViewWithTag(tag), item)
    }

    fun clearFields() {
        for (itemTag in tags.keys) {
            val type = tags.getValue(itemTag)
            managers[type]?.clear(findViewWithTag(itemTag))
        }
    }

    class CreateItemData(
        val itemType: PatternDataType,
        val tag: String,
        val prompt: Any?,
    )

    companion object {

        private const val DISTANCE_BETWEEN_FIELDS = 16
    }
}

enum class PatternDataType {

    TEXT,
    DOUBLE_TEXT,
    DATE_AND_TIME,
    DATE,
    RADIO,
    PICK,
    CHECK,
    HEADER,
    PROGRESS
}
