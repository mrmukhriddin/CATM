package ru.metasharks.catm.feature.qr.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.feature.qr.QrCodeAnalyzer
import ru.metasharks.catm.feature.qr.R
import ru.metasharks.catm.feature.qr.databinding.ActivityQrScannerBinding
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
internal class QrScannerActivity : BaseActivity() {

    private val viewModel: QrScannerViewModel by viewModels()
    private val binding: ActivityQrScannerBinding by viewBinding(CreateMethod.INFLATE)

    private lateinit var cameraExecutor: ExecutorService

    private var camera: Camera? = null
    private var prevBarcodeRawValue: String? = null

    private fun onQrScanned(barcode: Barcode) {
        prevBarcodeRawValue = when (prevBarcodeRawValue) {
            barcode.rawValue -> return
            else -> barcode.rawValue
        }
        val id: Int? = barcode.rawValue?.toIntOrNull()
        if (id == null) {
            CustomSnackbar.make(binding.root, R.string.invalid_qr_code, Snackbar.LENGTH_SHORT)
                .show()
            return
        }
        viewModel.tryToOpen(id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        setupToolbar()
        setupObservers()
        startCamera()
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showNavigate = true,
            showTitle = true,
            title = title.toString()
        )
    }

    private fun setupObservers() {
        viewModel.error.observe(this) {
            CustomSnackbar.make(binding.root, R.string.no_access, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    private fun startCamera() {
        lifecycleScope.launch {
            val cameraProvider = this@QrScannerActivity.getCameraProvider()

            val preview = Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(binding.cameraImage.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        QrCodeAnalyzer(this@QrScannerActivity::onQrScanned)
                    )
                }
            val currentCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this@QrScannerActivity,
                    currentCameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: IllegalStateException) {
                Timber.e(
                    "Use case has already been bound to another lifecycle or method is not called on main thread",
                    e
                )
            } catch (e: IllegalArgumentException) {
                Timber.e(
                    "The provided camera selector is unable to resolve a camera to be used for the given use cases",
                    e
                )
            }
        }
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { future ->
                future.addListener({
                    continuation.resume(future.get())
                }, ContextCompat.getMainExecutor(this@QrScannerActivity))
            }
        }

    override fun onBackPressed() {
        viewModel.exit()
    }
}
