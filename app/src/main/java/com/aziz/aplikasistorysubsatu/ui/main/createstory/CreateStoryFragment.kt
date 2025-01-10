package com.aziz.aplikasistorysubsatu.ui.main.createstory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.aziz.aplikasistorysubsatu.R
import com.aziz.aplikasistorysubsatu.data.Result
import com.aziz.aplikasistorysubsatu.databinding.FragmentCreateStoryBinding
import com.aziz.aplikasistorysubsatu.utils.ViewModelFactory
import com.aziz.aplikasistorysubsatu.utils.rotateBitmap
import com.aziz.aplikasistorysubsatu.utils.uriToFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import android.media.ExifInterface
import android.util.Log
import okhttp3.RequestBody.Companion.asRequestBody

class CreateStoryFragment : Fragment() {
    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding!!
    private val createStoryViewModel: CreateStoryViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btOpenCamera.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.cameraFragment))
        binding.btOpenGallery.setOnClickListener{
            startGallery()
        }
        binding.buttonAdd.setOnClickListener{
            uploadImage()
        }
        val fileUri = arguments?.get("selected_image")
        if (fileUri != null) {
            val uri: Uri = fileUri as Uri
            val isBackCamera = arguments?.getBoolean("isBackCamera") ?: false
            val result = rotateBitmap(
                BitmapFactory.decodeFile(uri.path),
                isBackCamera
            )
            getFile = uri.toFile()
            binding.ivImagePreview.setImageBitmap(result)
        }
    }
    private var getFile: File? = null
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            val fileUri = arguments?.get("selected_image")
            if (fileUri != null) {
                val uri: Uri = fileUri as Uri
                val isBackCamera = arguments?.getBoolean("isBackCamera") ?: false
                val result = rotateBitmap(
                    BitmapFactory.decodeFile(uri.path),
                    isBackCamera
                )
                getFile = uri.toFile()
                binding.ivImagePreview.setImageBitmap(result)
            }
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            var bitmap = BitmapFactory.decodeFile(myFile.path, options)
            try {
                val exif = ExifInterface(myFile.path)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                bitmap = rotateBitmap(bitmap, orientation)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            getFile = myFile
            binding.ivImagePreview.setImageBitmap(bitmap)
        }
    }
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
        }
        val radians = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90.0
            ExifInterface.ORIENTATION_ROTATE_180 -> 180.0
            ExifInterface.ORIENTATION_ROTATE_270 -> 270.0
            else -> 0.0
        } * Math.PI / 180.0
        matrix.postRotate(radians.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun uploadImage() {
        if (getFile != null) {
            showLoading(true)
            val rotatedBitmap = rotateBitmap(getFile!!)
            val rotatedFile = bitmapToFile(rotatedBitmap)
            val descriptionText = binding.edAddDescription.text
            if (!descriptionText.isNullOrEmpty()) {
                val description = descriptionText.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = rotatedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    rotatedFile.name,
                    requestImageFile
                )
                createStoryViewModel.postStory(imageMultipart, description).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Success -> {
                            showLoading(false)
                            Toast.makeText(context, result.data.message, Toast.LENGTH_LONG).show()
                            findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
                        }
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(context, result.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                showLoading(false)
                Toast.makeText(requireContext(), "Silakan masukkan deskripsi gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            showLoading(false)
            Toast.makeText(requireContext(), "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun bitmapToFile(rotatedBitmap: Bitmap): File {
        val tempFile = File.createTempFile("rotated_image", ".jpg")
        val outputStream = FileOutputStream(tempFile)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        outputStream.close()
        return tempFile
    }
    private fun showLoading(state: Boolean) {
        binding.pbCreateStory.isVisible = state
        binding.edAddDescription.isInvisible = state
        binding.ivImagePreview.isInvisible = state
        binding.btOpenCamera.isInvisible = state
        binding.btOpenGallery.isInvisible = state
        binding.buttonAdd.isInvisible = state
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun rotateBitmap(media: File): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        val inputStream = FileInputStream(media)
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        inputStream.close()
        val bitmap = BitmapFactory.decodeFile(media.absolutePath, options)
        var rotatedBitmap: Bitmap?
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        }
        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        fun isBitmapRecycled(bitmap: Bitmap): Boolean {
            return bitmap.isRecycled
        }
        fun processBitmap(filePath: String) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            if (isBitmapRecycled(bitmap)) {
                Log.d("Bitmap Processing", "Bitmap sudah direcycle")
            } else {
                Log.d("Bitmap Processing", "Bitmap belum direcycle")
                bitmap.recycle()
            }
        }
        val fileOutputStream = FileOutputStream(media)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream)
        fileOutputStream.flush()
        return rotatedBitmap
    }
    @Throws(IOException::class)
    private fun reduceFileImage() {
    }
}
private fun asRequestBody(toMediaTypeOrNull: MediaType?): RequestBody {
    return object : RequestBody() {
        override fun contentType(): MediaType? {
            return toMediaTypeOrNull
        }
        override fun contentLength(): Long {
            return 0
        }
        override fun writeTo(sink: BufferedSink) {
        }
    }
}

