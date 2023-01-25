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
import com.example.bitcard.db.database.MainDatabase
import com.example.bitcard.db.entities.User
import com.example.bitcard.fragments.SelectImageBottomSheetDialogFragment
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.UserDataSenderObj
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.result_launchers.PermissionResultGenerified
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val permissionResultGenerified = PermissionResultGenerified.registerForPermissionResult(this)
    private val database by lazy { MainDatabase.getInstance(this).userDao() }

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
                saveUserImage(
                    user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                    selectedImageBitmap
                )
            }
        }
    }

    private val selectImageFromGalleryResult =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profilePicture.setImageURI(it)
            val imageStream = contentResolver.openInputStream(uri)
            val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
            uploadUserImage(
                user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                selectedImageBitmap
            )
            saveUserImage(
                user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                selectedImageBitmap
            )
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

    private fun getUserData(user_id: Long){
       database.getUser(user_id).observe(this) {
           if(it != null && it.userId.isNotEmpty()) {
               render(it)
           }else{
               getUserDataFromHTTP(userId = user_id)
           }
       }
    }

    private fun getUserDataFromHTTP(userId: Long){

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
                                //save user to database
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.insert(it.data)
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

    private fun render(user: User){
        binding.emailTextView.text = user.email
        binding.streetAddressTextView.text = user.address
        binding.birthdayTextView.text = user.dateOfBirth
        binding.fullnameTextView.text = user.name + " " + user.surname
        binding.usernameTextView.text = user.username
        user.image?.let { encodedImage ->
            if (encodedImage.trim().isNotEmpty()) {
                binding.profilePicture.setImageBitmap(
                    decodeImageToBitmap(encodedImage)
                )
            }
        }
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
                    SelectImageBottomSheetDialogFragment.RESULT_CAMERA -> {
                        Log.i("selectImageBottomSheetDialogFragment", "camera selected!")
                        takeImage()
                        selectImageBottomSheetDialogFragment.dismiss()
                    }

                    SelectImageBottomSheetDialogFragment.RESULT_GALLERY -> {
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

        val registerModel= RegisterModel(UserDataSenderObj(
            image = imageB64
        ))

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

    private fun saveUserImage(user_id: Long, image: Bitmap){
        val imageB64 = encodeBitmapToBase64(image)
        CoroutineScope(Dispatchers.IO).launch {
            val tmpUser = database.get(user_id)
            tmpUser?.let {
                it.image = imageB64
                database.insert(tmpUser)
            }
        }
    }

    private fun decodeImageToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun encodeBitmapToBase64(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos) // bm is the bitmap object
        val byteArrayImage: ByteArray = baos.toByteArray()
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
    }



}