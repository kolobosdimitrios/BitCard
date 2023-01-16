package com.example.bitcard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.bitcard.databinding.ActivityProfileInfoBinding
import com.example.bitcard.fragments.SelectImageBottomSheetDialogFragment
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.UserModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.result_launchers.PermissionResultGenerified
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var userModel: UserModel
    private val permissionResultGenerified = PermissionResultGenerified.registerForPermissionResult(this)

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.profilePicture.setImageURI(uri)
                val imageStream = contentResolver.openInputStream(uri)
                val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
                uploadUserImage(
                    user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                    selectedImageBitmap
                )
            }
        }
    }

    private val selectImageFromGalleryResult =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profilePicture.setImageURI(it)
        }
    }

    private var latestTmpUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result -> handleResult(result)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.changePasswordTextview.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            launcher.launch(intent)
        }

        getUserData(
            SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id")
        )
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserData(userId: Long){

        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        bitcardApiV1.get(userId).enqueue(object : Callback<GetUserResponse> {

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if (response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if (it != null) {
                            if (it.status_code == SimpleResponse.STATUS_OK) {
                                //render
                                userModel = it.data
                                binding.emailTextView.text = it.data.email
                                binding.streetAddressTextView.text = it.data.address
                                binding.birthdayTextView.text = it.data.dateOfBirth
                                binding.fullnameTextView.text = it.data.name + " " +it.data.surname
                                binding.usernameTextView.text = it.data.username
                                it.data.image?.let { encodedImage ->
                                    binding.profilePicture.setImageBitmap(
                                        decodeImageToBitmap(encodedImage)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                TODO("Handle errors in http call")
            }
        })
    }

    private fun handleResult(result: ActivityResult?){
        if(result != null){
            when (result.resultCode) {
                ChangePasswordActivity.RESULT_PASSWORD_CHANGED -> {
                    Toast.makeText(this, "Password changed!", Toast.LENGTH_SHORT).show()
                }
                ChangePasswordActivity.RESULT_PASSWORD_NOT_CHANGED -> {
                    Toast.makeText(this, "Password not changed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onProfilePictureClick(view: View){
        val selectImageBottomSheetDialogFragment = SelectImageBottomSheetDialogFragment()
        selectImageBottomSheetDialogFragment.setOnResultListener(object : SelectImageBottomSheetDialogFragment.OnResultListener {
            override fun onResult(result: Int) {
                when (result){
                    SelectImageBottomSheetDialogFragment.Result.RESULT_CAMERA -> {
                        Log.i("selectImageBottomSheetDialogFragment", "camera selected!")
                        takeImage()
                        selectImageBottomSheetDialogFragment.dismiss()
                    }

                    SelectImageBottomSheetDialogFragment.Result.RESULT_GALLERY -> {
                        Log.i("selectImageBottomSheetDialogFragment", "gallery selected!")
                        chooseImage()
                        selectImageBottomSheetDialogFragment.dismiss()
                    }
                }
            }

        })

        selectImageBottomSheetDialogFragment.show(supportFragmentManager, "Select image source modal fragment")
    }

    private fun takeImage(){

        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if(cameraPermission == PackageManager.PERMISSION_GRANTED){
            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let { uri ->
                    latestTmpUri = uri
                    takeImageResult.launch(uri)
                }
            }
        }else{
            permissionResultGenerified.ask(Manifest.permission.CAMERA, object : PermissionResultGenerified.OnPermissionResult<Boolean> {
                override fun onPermissionResult(result: Boolean) {
                    if ( result ){
                        lifecycleScope.launchWhenStarted {
                            getTmpFileUri().let { uri ->
                                latestTmpUri = uri
                                takeImageResult.launch(uri)
                            }
                        }
                    }else{
                        //TODO: Prompt User and explain!
                    }
                }

            })
        }

    }

    private fun chooseImage(){
        val storagePermissionResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if( storagePermissionResult == PackageManager.PERMISSION_GRANTED ){
            getImageFromGallery()
        }else{

            permissionResultGenerified.ask(Manifest.permission.READ_EXTERNAL_STORAGE, object : PermissionResultGenerified.OnPermissionResult<Boolean> {
                override fun onPermissionResult(result: Boolean) {
                    if ( result ){
                        getImageFromGallery()
                    }else{
                        //TODO: Prompt User and explain!
                    }
                }

            })
        }
    }

    private fun getImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun uploadUserImage(user_id: Long, image: Bitmap){
        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        val imageB64 = encodeBitmapToBase64(image)

        userModel.image = imageB64

        val registerModel= RegisterModel(userModel)

        bitcardApiV1.updateUsersProfilePicture(user_id = user_id, registerModel = registerModel).enqueue(object : Callback<SimpleResponse>{
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if(response.isSuccessful) {
                    Log.i("response", "successful")
                    response.body()?.toString()?.let { Log.i("response data", it) }
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                t.localizedMessage?.let { Log.e("Image upload failed", it) }
            }

        })
    }

    private fun getImagePath(uri: Uri) : String?{

        return uri.path
    }

    private fun getImageFileFromUriPath(uri_path: String) : File {
        return File(uri_path)
    }

    private fun decodeImageToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun encodeBitmapToBase64(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos) // bm is the bitmap object
        val byteArrayImage: ByteArray = baos.toByteArray()
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
    }



}