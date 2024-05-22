package ru.metasharks.catm.core.ui.recycler.space

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Содержит ограничения, которые определяются может ли декорация применена к определенному
 * потомку ресайклера
 */
interface DecorateRule : Comparable<DecorateRule> {
    /**
     * Определяет, нужно ли декорировать
     *
     * @param view   - потомок recyclerView для декорирования
     * @param parent - recyclerView
     * @return true, еслм вью должна быть декорирована
     */
    fun isDecorated(view: View, parent: RecyclerView): Boolean

    /**
     * Вес используется для сравнения разные DecorateRule реализаций
     *
     * @return вес правила для сравнения
     */
    fun ruleWeight(): Int

    override fun compareTo(o: DecorateRule): Int {
        return ruleWeight().compareTo(o.ruleWeight())
    }
}
