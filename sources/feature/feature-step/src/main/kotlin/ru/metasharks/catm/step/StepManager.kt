package ru.metasharks.catm.step

import android.view.View
import androidx.viewbinding.ViewBinding
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.step.validator.Validator

/**
 * Binding - тип binding для этого класса
 * Output - тип данных, нужных для бизнес-логикик (id, строки и т. д.)
 * Base - тип данных, нужных для восстановления поля (имена, строки и т. д.)
 */
abstract class StepManager<Binding : ViewBinding, Output, Base>(
    protected val binder: (View) -> Binding,
) {

    // нет необходимости перегружать
    open fun setValue(view: View, valueToSet: Any?) = Unit

    open fun showError(view: View, error: Validator.Result) = Unit

    abstract fun signToStatusListener(view: View, tag: String, statusListener: StatusListener)

    abstract fun gatherDataFromView(view: View): Output?

    abstract fun gatherRestoreDataFromView(view: View): Base?

    abstract fun clear(view: View)
}
