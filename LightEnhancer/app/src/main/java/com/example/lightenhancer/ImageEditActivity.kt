package com.example.lightenhancer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.lightenhancer.databinding.ActivityImageEditBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding
    private lateinit var buttonVisibilityHandler: ButtonVisibilityHandler

    private var executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageSaver = ImageSaver(this)
    private var lightEnhancer = LightEnhancer()

    companion object {
        const val IMAGE_REQUEST_CODE = 1_000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.enhanceImageButton.visibility = View.GONE
        binding.saveImageButton.visibility = View.GONE
        binding.loadingPanel.visibility = View.GONE

        binding.imageSelectButton.setOnClickListener {
            selectImage()
        }

        binding.saveImageButton.setOnClickListener {
            saveImage()
        }

        lightEnhancer.enhancerAddress = lightEnhancer.initEnhancer(assets, Constants.MODEL_PATHS[2])

        val mainHandler = Handler(Looper.getMainLooper())

        binding.enhanceImageButton.setOnClickListener {
            var bitmap = binding.imageView.drawable.toBitmap()
            binding.loadingPanel.visibility = View.VISIBLE

            executorService.execute {
                lightEnhancer.enhanceFullResolution(
                    bitmap,
                    Constants.MODEL_SIZES[2],
                    lightEnhancer.enhancerAddress
                )

                mainHandler.post {
                    binding.imageView.setImageBitmap(bitmap)
                    binding.loadingPanel.visibility = View.GONE
                    Toast.makeText(
                        this@ImageEditActivity,
                        "Image enhanced",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        buttonVisibilityHandler = ButtonVisibilityHandler(
            listOf(binding.enhanceImageButton, binding.saveImageButton, binding.imageSelectButton),
            binding.root,
            5000
        )
    }

    private fun saveImage() {
        val bitmap = binding.imageView.drawable.toBitmap()
        imageSaver.saveImageToGallery(bitmap, 0)
        Toast.makeText(
            this@ImageEditActivity,
            "Image saved to gallery",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.imageView.setImageURI(data?.data)
            binding.imageView.tag = data?.data
            binding.enhanceImageButton.visibility = View.VISIBLE
            binding.saveImageButton.visibility = View.VISIBLE
            buttonVisibilityHandler.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdownNow()
    }
}