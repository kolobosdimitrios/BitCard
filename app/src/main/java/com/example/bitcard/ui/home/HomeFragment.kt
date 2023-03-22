package com.example.bitcard.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bitcard.R
import com.example.bitcard.databinding.FragmentHomeBinding
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.db.entities.User
import com.example.bitcard.globals.QR

class HomeFragment : Fragment() {

    private lateinit var binder: FragmentHomeBinding
    private val homeFragmentViewModel : HomeFragmentViewModel by activityViewModels() {defaultViewModelProviderFactory}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentHomeBinding.inflate(inflater)

        homeFragmentViewModel.selectedUser.observe(viewLifecycleOwner){
            user -> renderLayoutWithUserData(user)
        }

        homeFragmentViewModel.selectedToken.observe(viewLifecycleOwner){ token ->
            drawQr(binder.cardImageView, token.token)

        }

        homeFragmentViewModel.selectedCoupons.observe(viewLifecycleOwner){
            coupons -> renderCouponsAndPoints(coupons)
        }
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun renderCouponsAndPoints(coupons: List<Coupon>){

        binder.availableCouponsTextView.text = coupons.size.toString()

    }

    private fun renderLayoutWithUserData(user: User){

        Log.e("user data", user.toString())

        val nameSurname = String.format(resources.getString(R.string.space_between), user.name, user.surname)

        binder.username.text = nameSurname
        binder.pointsTextView.text = user.points.toString()
        binder.nextRewardTextView.text = user.remainingPoints.toString()
        user.image?.let {
            if(it.trim().isNotEmpty()) {
                val bmp = decodeImageToBitmap(it)
                binder.profilePicture.setImageBitmap(bmp)
            }
        }


    }

    private fun decodeImageToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun drawQr(imageView : ImageView, content: String){
        val bmp = QR.generate(content, requireContext().getColor(R.color.primaryDarkColor), fetchPrimaryColor())
        imageView.setImageBitmap(bmp)
    }

    private fun fetchPrimaryColor(): Int {
        val a = TypedValue()
        requireActivity().theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        val color : Int = if (a.isColorType) {
            // windowBackground is a color
            a.data
        }else{
            Color.MAGENTA
        }

        return color
    }


}