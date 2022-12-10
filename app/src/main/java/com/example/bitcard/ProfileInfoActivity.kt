package com.example.bitcard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.example.bitcard.network.daos.requests.UserIdModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.UsersApi
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.result_launchers.PermissionResultGenerified
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val permissionResultGenerified = PermissionResultGenerified.registerForPermissionResult(this)
    private lateinit var firebaseUser: FirebaseUser

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.profilePicture.setImageURI(uri)
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

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        getUserData(firebaseUser.uid)

        binding.changePasswordTextview.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            launcher.launch(intent)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserData(userId: String){

        val userIdModel = UserIdModel(userId)

        val usersApi = RetrofitHelper.getRetrofitInstance().create(UsersApi::class.java)

        usersApi.get(userIdModel.userId).enqueue(object : Callback<GetUserResponse> {

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if (response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if (it != null) {
                            if (it.status_code == SimpleResponse.STATUS_OK.toLong()) {
                                //render
                                binding.emailTextView.text = it.data.email
                                binding.streetAddressTextView.text = it.data.address
                                binding.birthdayTextView.text = it.data.dateOfBirth
                                binding.fullnameTextView.text = it.data.name + " " +it.data.surname
                                binding.usernameTextView.text = it.data.username
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



}