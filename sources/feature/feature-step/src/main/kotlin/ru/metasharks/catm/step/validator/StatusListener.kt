package ru.metasharks.catm.step.validator

class StatusListener(
    private val onFormFilledActionProvider: () -> ((Boolean) -> Unit)?
) {

    val enabled: Boolean
        get() = listOfNecessaryTags.isNotEmpty()

    var type: Int = ALL

    private var listOfNecessaryTags: Set<String> = emptySet()

    private val listOfNotFilledTags: MutableSet<String> = mutableSetOf()

    fun clear() {
        listOfNecessaryTags = emptySet()
        listOfNotFilledTags.clear()
    }

    fun setNecessaryTags(tagSet: Set<String>) {
        listOfNecessaryTags = tagSet
        listOfNotFilledTags.clear()
        listOfNotFilledTags.addAll(listOfNecessaryTags)
    }

    fun update(tag: String, isFilled: Boolean) {
        if (!enabled) {
            return
        }
        if (!listOfNecessaryTags.contains(tag)) {
            return
        }
        var updated = false
        if (isFilled && listOfNotFilledTags.contains(tag)) {
            updated = true
            listOfNotFilledTags.remove(tag)
        } else if (!isFilled && !listOfNotFilledTags.contains(tag)) {
            updated = true
            listOfNotFilledTags.add(tag)
        }
        if (updated) {
            when (type) {
                ALL -> checkIfFormFilled()
                AT_LEAST_ONE -> checkIfAtLeastOneFilled()
            }
        }
    }

    private fun checkIfAtLeastOneFilled() {
        val onFormFilledAction = onFormFilledActionProvider() ?: return
        if (listOfNecessaryTags.size - listOfNotFilledTags.size > 0) {
            onFormFilledAction(true)
        } else {
            onFormFilledAction(false)
        }
    }

    private fun checkIfFormFilled() {
        val onFormFilledAction = onFormFilledActionProvider() ?: return
        if (listOfNotFilledTags.isEmpty()) {
            onFormFilledAction(true)
        } else {
            onFormFilledAction(false)
        }
    }

    companion object {

        const val ALL = 1
        const val AT_LEAST_ONE = 2
    }
}
