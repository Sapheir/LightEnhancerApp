package com.example.lightenhancer

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.lightenhancer.databinding.ActivityMainBinding

class CameraHandler(private val activity: AppCompatActivity, private val binding: ActivityMainBinding, private val realtimeAnalyzer: RealtimeAnalyzer) {

    private var imageCapture: ImageCapture? = null
    private var imageSaver = ImageSaver(activity)

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.camera.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(getTargetResolution())
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(activity), realtimeAnalyzer)
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    activity, CameraSelector.DEFAULT_BACK_CAMERA,
                    preview, imageCapture, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e(Constants.TAG, "startCamera fail: ", e)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun toggleTorchLight() {
        Log.d("Torch", "Toggled")
    }

    fun captureCamera() {
        val rotationDegrees = 90 - binding.camera.display.rotation
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (binding.settingsPopup.enhanceSwitch.isChecked)
                        realtimeAnalyzer.lightEnhancer.enhanceFullResolution(bitmap, Constants.MODEL_SIZES[binding.settingsPopup.modelsSpinner.selectedItemPosition], realtimeAnalyzer.lightEnhancer.enhancerAddress)
                    imageSaver.saveImageToGallery(bitmap, rotationDegrees)
                    image.close()
                    Toast.makeText(
                        activity,
                        "Image saved to gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG, "Image capture failed", exception)
                }
            }
        )
    }

    private fun getTargetResolution(): Size {
        return when (activity.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Size(240, 320)
            Configuration.ORIENTATION_LANDSCAPE -> Size(320, 240)
            else -> Size(320, 240)
        }
    }
}