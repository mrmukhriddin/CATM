package ru.metasharks.catm.core.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ru.metasharks.catm.core.ui.R

class WindowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var windowViewId: Int = -1
    private val windowCoords: IntArray = intArrayOf(-1, -1)
    private val viewCoords: IntArray = intArrayOf(-1, -1)
    private var windowBitmap: Bitmap? = null

    private val windowView: View by lazy {
        findViewById(windowViewId)
    }
    private lateinit var windowDrawable: Drawable

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        getValuesFromAttrs(attrs)
    }

    override fun onDraw(canvas: Canvas) {
        if (windowView.isVisible) {
            windowBitmap = checkBitmap(windowView, windowDrawable, windowBitmap)
            getLocationInWindow(viewCoords)
            windowView.getLocationInWindow(windowCoords)
            val x = windowCoords[0] - viewCoords[0]
            val y = windowCoords[1] - viewCoords[1]
            canvas.drawBitmap(requireNotNull(windowBitmap), x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun getValuesFromAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.WindowView)
        windowViewId = typedArray.getResourceId(R.styleable.WindowView_view, -1)
        val drawableId = typedArray.getResourceId(R.styleable.WindowView_drawable, -1)
        windowDrawable = ContextCompat.getDrawable(context, drawableId)
            ?: throw IllegalArgumentException("No such view by id $drawableId")
        typedArray.recycle()
    }

    private fun checkBitmap(view: View, drawable: Drawable, bitmap: Bitmap?): Bitmap {
        val width = view.width - (view.paddingLeft + view.paddingRight)
        val height = view.height - (view.paddingTop + view.paddingBottom)
        return if (bitmap == null || bitmap.width != width || bitmap.height != height) bitmapFromDrawable(
            drawable,
            view.width,
            view.height
        ) else bitmap
    }

    private fun bitmapFromDrawable(drawable: Drawable, width: Int, height: Int): Bitmap {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            if (width == bitmap.width && height == bitmap.height) {
                return bitmap
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
