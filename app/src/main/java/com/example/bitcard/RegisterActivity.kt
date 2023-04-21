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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.bitcard.databinding.ActivityRegisterBinding
import com.example.bitcard.fragments.SelectImageBottomSheetDialogFragment
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.UserDataSenderObj
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.result_launchers.PermissionResultGenerified
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private val permissionResultGenerified = PermissionResultGenerified.registerForPermissionResult(this)
    private var latestTmpUri: Uri? = null
    private var selectedImageBitmapEncoded: Bitmap? = null

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.profilePicture.setImageURI(uri)
                val imageStream = contentResolver.openInputStream(uri)
                selectedImageBitmapEncoded = BitmapFactory.decodeStream(imageStream)

            }
        }
    }

    private val selectImageFromGalleryResult =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

        uri?.let {
            binding.profilePicture.setImageURI(it)
            val imageStream = contentResolver.openInputStream(uri)
            selectedImageBitmapEncoded = BitmapFactory.decodeStream(imageStream)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        binding.date.onFocusChangeListener = this
        binding.registerBtn.setOnClickListener {
            if(valuesCorrect()) registerUser()
        }

    }

    private fun valuesCorrect(): Boolean{
        val username = binding.username.text.toString().trim()
        val password =  binding.password.text.toString().trim()
        val passwordConfirmation = binding.verifyPassword.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val surname = binding.surname.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val dateOfBirth = binding.date.text.toString().trim()
        val address = binding.address.text.toString().trim()

        if(name.isEmpty()) { binding.name.error = getString(R.string.name_cannot_be_empty); return false;}
        if(surname.isEmpty()) { binding.surname.error = getString(R.string.surname_cannot_be_empty); return false;}
        if(email.isEmpty()) { binding.username.error = getString(R.string.email_cannot_be_empty); return false;}
        if(username.isEmpty()) { binding.username.error = getString(R.string.username_cannot_be_empty); return false;}
        if(password.isEmpty()) { binding.password.error = getString(R.string.password_cannot_be_empty); return false; }
        if(passwordConfirmation.isEmpty()) { binding.verifyPassword.error = getString(R.string.password_cannot_be_empty); return false;}
        if(dateOfBirth.isEmpty()) { binding.date.error = getString(R.string.date_of_birth_cannot_be_empty); return false;}
        if(address.isEmpty()) { binding.address.error = getString(R.string.address_cannot_be_empty); return false;}

        if(password.length <= 6){
            binding.password.error = getString(R.string.password_must_have_6_plus_digits)
            binding.password.text.clear()
            binding.verifyPassword.text.clear()
            return false
        }

        if(password != passwordConfirmation){
            binding.password.text.clear()
            binding.verifyPassword.text.clear()
            binding.verifyPassword.error = getString(R.string.wrong_password_confirmation)
            return false
        }
        return true

    }

    private fun registerUser(){

        auth.createUserWithEmailAndPassword(binding.email.text.toString().trim(), binding.password.text.toString().trim())
            .addOnCompleteListener { result ->
                Log.i("Register action", "Started")
                if (result.isSuccessful) {
                    Log.i("User register", "successful")
                    result.result.user?.uid?.let {
                        Log.i("User ID", it)
                        val registerUserModel = UserDataSenderObj(
                            name = binding.name.text.toString(),
                            surname = binding.surname.text.toString(),
                            username = binding.username.text.toString(),
                            email = binding.email.text.toString(),
                            userId = it,
                            dateOfBirth = binding.date.text.toString(),
                            address = binding.address.text.toString(),
                            id = null,
                            image = encodeBitmapToBase64(
                                selectedImageBitmapEncoded
                            )
                        )

                        sendCreateUserRequest(registerUserModel)
                    }


                } else if (result.isCanceled) {
                    Log.e("Register action", "Cancelled")
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }


    }

    private fun sendCreateUserRequest(user: UserDataSenderObj){
        val register = RegisterModel(user)

        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        bitcardApiV1.register(register).enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if(it != null){
                            if(it.status_code == SimpleResponse.STATUS_OK){
                                SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_DATA, "id", it.data.id)
                                Log.i("user created", it.data.toString())
                                val intent = Intent(applicationContext, MainActivityBottomNavigation::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }

                Toast.makeText(applicationContext, "User created", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "User not created", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun selectDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date_dialog_header))
                .build()

        datePicker.addOnPositiveButtonClickListener {
            binding.date.setText(getDateStringFormatted(it))
        }
        datePicker.show(supportFragmentManager, null)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1){ //has focus
            selectDate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivityBottomNavigation::class.java))
        finish()
    }

    private fun getDateStringFormatted(millis: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return simpleDateFormat.format(millis)
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

    private fun encodeBitmapToBase64(bmp: Bitmap?): String? {
        bmp?.let {
            val baos = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 25, baos) // bm is the bitmap object
            val byteArrayImage: ByteArray = baos.toByteArray()
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
        }
       return null
    }
}