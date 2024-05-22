package ru.metasharks.catm.media.preview.photo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.media.preview.R
import ru.metasharks.catm.media.preview.databinding.FragmentPhotoBinding
import ru.metasharks.catm.utils.argumentDelegate
import timber.log.Timber

@AndroidEntryPoint
class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private val fileUri: String by argumentDelegate(ARG_FILE_URI)
    private val binding: FragmentPhotoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(fileUri)
        Glide.with(requireContext())
            .load(fileUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.root)
    }

    companion object {

        private const val ARG_FILE_URI = "file_uri"

        fun newInstance(fileUri: String): PhotoFragment {
            val args = Bundle()
            args.putString(ARG_FILE_URI, fileUri)
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
