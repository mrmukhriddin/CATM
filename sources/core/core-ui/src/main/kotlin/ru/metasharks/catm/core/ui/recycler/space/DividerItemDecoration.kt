package ru.metasharks.catm.core.ui.recycler.space

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.Px
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(
    @Px private val dividerTotalSize: Int,
    private val divider: Drawable? = null,
    private val orientation: Int = HORIZONTAL,
    private val showDividersFlag: Int = SHOW_MIDDLE,
    private val decorationRules: List<DecorateRule>? = null
) : RecyclerView.ItemDecoration() {

    init {
        require(orientation in setOf(VERTICAL, HORIZONTAL))
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        when (orientation) {
            HORIZONTAL -> drawHorizontal(parent, c)
            VERTICAL -> drawVertical(parent, c)
        }
    }

    private fun drawHorizontal(parent: RecyclerView, c: Canvas) {
        if (divider == null) return

        val dividerHeight = if (divider.intrinsicHeight > 0) {
            divider.intrinsicHeight
        } else {
            parent.height
        }
        val top = parent.height / 2 - dividerHeight / 2
        val bottom = top + dividerHeight
        for (i in 0 until parent.childCount) {
            val child: View = parent[i]
            if (isDecorated(child, parent)) {
                if (showStart(i)) {
                    val left =
                        child.left - dividerTotalSize / 2 - divider.intrinsicWidth / 2
                    val right = left + divider.intrinsicWidth
                    divider.dividerDraw(c, left, top, right, bottom)
                }
                if (doNotShowEndOrMiddle(parent, i)) continue
                val left = child.right + dividerTotalSize / 2 - divider.intrinsicWidth / 2
                val right = left + divider.intrinsicWidth
                divider.dividerDraw(c, left, top, right, bottom)
            }
        }
    }

    private fun drawVertical(parent: RecyclerView, c: Canvas) {
        if (divider == null) return

        val dividerWidth = if (divider.intrinsicWidth > 0) {
            divider.intrinsicWidth
        } else {
            parent.width
        }
        val left = parent.width / 2 - dividerWidth / 2
        val right = left + dividerWidth
        for (i in 0 until parent.childCount) {
            val child = parent[i]
            if (isDecorated(child, parent)) {
                if (showStart(i)) {
                    val top = child.top - dividerTotalSize / 2 - divider.intrinsicHeight / 2
                    val bottom = top + divider.intrinsicHeight
                    divider.dividerDraw(c, left, top, right, bottom)
                }
                if (doNotShowEndOrMiddle(parent, i)) continue
                val top = child.bottom + dividerTotalSize / 2 - divider.intrinsicHeight / 2
                val bottom = top + divider.intrinsicHeight
                divider.dividerDraw(c, left, top, right, bottom)
            }
        }
    }

    private fun doNotShowEndOrMiddle(parent: RecyclerView, position: Int): Boolean {
        return position == parent.childCount - 1 && !checkFlag(showDividersFlag, SHOW_END) ||
                position < parent.childCount - 1 && !checkFlag(showDividersFlag, SHOW_MIDDLE)
    }

    private fun showStart(position: Int): Boolean {
        return position == 0 && checkFlag(showDividersFlag, SHOW_START)
    }

    private fun Drawable.dividerDraw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        setBounds(left, top, right, bottom)
        draw(canvas)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (isDecorated(view, parent)) {
            val adapterPosition = parent.getChildAdapterPosition(view)
            val checkStart = adapterPosition == 0 && checkFlag(showDividersFlag, SHOW_START)
            val checkEnd =
                adapterPosition == state.itemCount - 1 && checkFlag(showDividersFlag, SHOW_END)
            val checkMiddle =
                adapterPosition < state.itemCount - 1 && checkFlag(showDividersFlag, SHOW_MIDDLE)
            if (checkStart) {
                when (orientation) {
                    HORIZONTAL -> outRect.left = dividerTotalSize
                    VERTICAL -> outRect.top = dividerTotalSize
                }
            }
            if (checkMiddle || checkEnd) {
                when (orientation) {
                    HORIZONTAL -> outRect.right = dividerTotalSize
                    VERTICAL -> outRect.bottom = dividerTotalSize
                }
            }
        }
    }

    /**
     * Проверяет, разрешена ли декорация для данного конкретного предмета
     * По умолчания (если нет никаких правил), декорирование разрешено для всех предметов
     *
     * @param view   ненуллевой потомок
     * @param parent ненуллевой RecyclerView
     * @return `true` если декорирование разрешается
     */
    private fun isDecorated(view: View, parent: RecyclerView): Boolean {
        if (decorationRules.isNullOrEmpty()) {
            return true // The only rule is that there are no rules!
        }
        for (rule in decorationRules) {
            if (rule.isDecorated(view, parent)) {
                return true
            }
        }
        return false
    }

    companion object {
        const val VERTICAL = 0
        const val HORIZONTAL = 1

        const val SHOW_START = 1
        const val SHOW_MIDDLE = 1 shl 1
        const val SHOW_END = 1 shl 2

        private fun checkFlag(showDivider: Int, flag: Int): Boolean {
            return (showDivider and flag) == flag
        }
    }
}
