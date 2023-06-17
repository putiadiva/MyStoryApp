package com.example.mystoryapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.*
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.data.remote.response.Response
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.databinding.ActivityAddStoryBinding
import com.example.mystoryapp.viewmodel.AddStoryViewModel
import com.example.mystoryapp.viewmodel.UserPreferences
import com.example.mystoryapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authentication")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var viewModel: AddStoryViewModel
    private var token = ""

    private var getFile: File? = null

    companion object {
        private const val TAG = "Add Story Activity"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { upload() }

        val pref = UserPreferences.getInstance(dataStore)
        val repo = StoryRepository()
        viewModel = ViewModelProvider(this, ViewModelFactory(pref, repo)).get(
            AddStoryViewModel::class.java
        )
        viewModel.getToken().observe(this) { res ->
            token = res
        }
    }

    private fun upload() {
        val description = binding.edtDescription.text.toString()
        if (getFile == null || description.isEmpty()) {
            Toast.makeText(
                this@AddStoryActivity,
                "Lengkapi data story terlebih dahulu.",
                Toast.LENGTH_LONG)
            .show()
        } else {
            val description2 = description.toRequestBody("text/plain".toMediaType())
            val photo = reduceFileImage(getFile as File)
            val photo2 = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                photo.name,
                photo2
            )
            val auth = "Bearer $token"
            val client = ApiConfig.getApiService().addNewStory(auth, imageMultipart, description2)
            client.enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    Log.i (TAG, "Upload with token ${token}")
                    if (response.isSuccessful) {
                        // toast
                        Toast.makeText(
                            this@AddStoryActivity,
                            "Berhasil menambahkan story.",
                            Toast.LENGTH_LONG
                        ).show()
                        // navigate ke main actvt
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture.")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
//            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
//            getFile = myFile
//            binding.ivPreview.setImageURI(selectedImg)
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                rotateFile(myFile, true)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.mystoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            // supaya digunakan kamera belakang:
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val cameraInfo = Camera.CameraInfo()
            val cameraId = intent.extras?.getInt("android.intent.extras.CAMERA_FACING") ?: Camera.CameraInfo.CAMERA_FACING_BACK
            Camera.getCameraInfo(cameraId, cameraInfo)
            val isBackCamera = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK
            myFile.let { file ->
                rotateFile(file, isBackCamera)
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
            getFile = myFile

            // delete later
//            val myFile = File(currentPhotoPath)
//            getFile = myFile
//
//            val result = BitmapFactory.decodeFile(getFile?.path)
//            binding.ivPreview.setImageBitmap(result)
        }
    }


}