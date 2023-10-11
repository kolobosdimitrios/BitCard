package com.example.bitcard.ui.fragments.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bitcard.ui.activities.ProfileInfoActivity
import com.example.bitcard.R
import com.example.bitcard.databinding.FragmentHomeBinding
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.db.entities.User
import com.example.bitcard.BitmapHandler
import com.example.bitcard.QR

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


        binder.profilePicture.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileInfoActivity::class.java))
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


        val nameSurname = String.format(resources.getString(R.string.space_between), user.name, user.surname)

        binder.username.text = nameSurname
        binder.pointsTextView.text = user.points.toString()
        binder.nextRewardTextView.text = user.remainingPoints.toString()
        user.image?.let {
            if(it.trim().isNotEmpty()) {
                val bmp = BitmapHandler.decodeImageToBitmap(it)
                binder.profilePicture.setImageBitmap(bmp)
            }
        }


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