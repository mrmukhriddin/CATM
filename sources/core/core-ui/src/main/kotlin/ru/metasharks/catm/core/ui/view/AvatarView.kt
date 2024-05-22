package ru.metasharks.catm.core.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.databinding.LayoutAvatarBinding
import ru.metasharks.catm.core.ui.utils.getColorForChar
import ru.metasharks.catm.core.ui.utils.getFirstLetters

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var loadFactory: ((ImageView) -> Unit) = { imageView ->
        Glide.with(context)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_circle)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onImageLoadError()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean = false
            })
            .transform(CircleCrop())
            .into(imageView)
    }

    private val onImageLoadError = {
        loadLetters()
    }

    private val binding: LayoutAvatarBinding by lazy {
        LayoutAvatarBinding.bind(this)
    }

    private var innerPadding: Float = 0f
    private var avatarUrl: String? = null
    private var fullName: String? = null

    init {
        inflate(context, R.layout.layout_avatar, this)
        getValuesFromAttrs(attrs)
    }

    private fun getValuesFromAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.AvatarView)
        innerPadding = typedArray.getDimension(
            R.styleable.AvatarView_innerPadding,
            0f
        )
        typedArray.recycle()
    }

    fun setData(fullName: String, avatarUrl: String? = null): AvatarView {
        this.fullName = fullName
        this.avatarUrl = avatarUrl
        return this
    }

    fun setLoadFactory(loadFactory: (ImageView) -> Unit) {
        this.loadFactory = loadFactory
    }

    fun load() {
        if (avatarUrl != null) {
            loadImage()
        } else {
            loadLetters()
        }
        invalidate()
    }

    private fun loadLetters() {
        binding.avatarImg.isGone = true
        binding.avatarLetters.isVisible = true
        val letters = fullName.getFirstLetters(2)
        binding.avatarLetters.setPadding(0, innerPadding.toInt(), 0, innerPadding.toInt())
        binding.avatarLetters.text = letters
        binding.avatarLetters.setTextColor(Color.WHITE)
        binding.avatarLetters.background.setTint(context.getColorForChar(letters!!.first()))
    }

    private fun loadImage() {
        binding.avatarImg.isVisible = true
        binding.avatarLetters.isGone = true
        loadFactory.invoke(binding.avatarImg)
    }
}
