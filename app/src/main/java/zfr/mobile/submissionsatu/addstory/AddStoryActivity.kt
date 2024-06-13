package zfr.mobile.submissionsatu.addstory

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import zfr.mobile.submissionsatu.SessionManager
import zfr.mobile.submissionsatu.api.ApiClient
import zfr.mobile.submissionsatu.databinding.ActivityAddStoryBinding
import zfr.mobile.submissionsatu.story.StoryActivity
import zfr.mobile.submissionsatu.story.StoryRepository
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val authToken = sessionManager.getAuthToken()
        if (authToken.isNullOrEmpty()) {
            finish()
        }

        val repository = StoryRepository(ApiClient.getApiService(authToken!!))
        val viewModelFactory = AddStoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddStoryViewModel::class.java)

        binding.ivAddPhoto.setOnClickListener {
            openGallery()
        }

        binding.btnSelectGallery.setOnClickListener {
            openGallery()
        }

        binding.btnSelectCamera.setOnClickListener {
            openCamera()
        }

        binding.buttonAdd.setOnClickListener {
            uploadStory()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            selectedImageFile = selectedImageUri?.let { uriToFile(it) }?.let { compressImage(it) }

            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.ivAddPhoto)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val photoBitmap = data?.extras?.get("data") as? Bitmap
            photoBitmap?.let {
                selectedImageFile = bitmapToFile(it)?.let { compressImage(it) }
                selectedImageUri = Uri.fromFile(selectedImageFile)

                Glide.with(this)
                    .load(photoBitmap)
                    .into(binding.ivAddPhoto)
            }
        }
    }


    private fun uploadStory() {
        val descriptionText = binding.edAddDescription.text.toString().trim()

        if (selectedImageFile != null && descriptionText.isNotBlank()) {
            if (selectedImageFile!!.length() > 1 * 1024 * 1024) {
                Toast.makeText(this, "File size should be less than 1MB", Toast.LENGTH_SHORT).show()
                return
            }
            val requestFile = selectedImageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body =
                MultipartBody.Part.createFormData("photo", selectedImageFile!!.name, requestFile)
            val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.addStory(description, body).observe(this) { success ->
                if (success) {
                    val intent = Intent(this, StoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to upload story", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Please select an image and enter a description",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "temp_file"

        val tempFile = File(cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

    private fun bitmapToFile(bitmap: Bitmap): File? {
        val filesDir = applicationContext.filesDir
        val imageFile = File(filesDir, "image_${System.currentTimeMillis()}.jpg")

        var os: OutputStream? = null
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            os?.close()
        }

        return imageFile
    }

    private fun compressImage(imageFile: File): File {
        val compressedFile = File(cacheDir, "compressed_${imageFile.name}")
        val bitmap = BitmapFactory.decodeFile(imageFile.path)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }

        return compressedFile
    }

}
