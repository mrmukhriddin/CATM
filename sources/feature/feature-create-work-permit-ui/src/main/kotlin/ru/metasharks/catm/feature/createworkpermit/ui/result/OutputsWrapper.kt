package ru.metasharks.catm.feature.createworkpermit.ui.result

import kotlin.reflect.KClass

data class OutputsWrapper(
    val outputs: MutableList<Any> = mutableListOf(),
) {

    fun addOutput(output: Any) {
        val oldList = outputs
        var indexOfItem = oldList.indexOfFirst { it.javaClass == output.javaClass }
        if (indexOfItem != -1) {
            outputs.removeAt(indexOfItem)
        } else {
            indexOfItem = outputs.size
        }
        outputs.add(indexOfItem, output)
    }

    operator fun <T : Any> get(clazz: KClass<T>): T? {
        return outputs.find { it::class == clazz } as T?
    }

    fun getListOf(vararg clazz: KClass<out Any>): List<Any> {
        val mutableList = mutableListOf<Any>()
        for (each in clazz) {
            this[each]?.let {
                mutableList.add(it)
            }
        }
        return mutableList.toList()
    }
}
