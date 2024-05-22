package ru.metasharks.catm.step

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ru.metasharks.catm.utils.layoutInflater

abstract class StepFactory<Binding : ViewBinding, Prompt : Any?>(
    val parent: ViewGroup,
    private val resInt: Int,
    private val binder: (View) -> Binding,
) {

    lateinit var view: View
    lateinit var binding: Binding

    val context: Context
        get() = parent.context

    open fun createView(prompt: Prompt): View {
        view = parent.context.layoutInflater.inflate(resInt, parent, false)
        binding = binder(view)
        return view
    }

    open fun validateField(view: View, validateCallback: ((Boolean) -> Unit)?): Boolean {
        binding = binder(view)
        return false
    }
}
