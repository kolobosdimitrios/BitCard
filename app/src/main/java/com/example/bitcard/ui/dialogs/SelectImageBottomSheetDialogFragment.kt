package com.example.bitcard.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.bitcard.R
import com.example.bitcard.databinding.SelectImageBottomSheetDialogFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectImageBottomSheetDialogFragment: BottomSheetDialogFragment() {

    companion object Result{
        const val RESULT_CAMERA = 1
        const val RESULT_GALLERY = 2
    }

    interface OnResultListener{

        fun onResult(result: Int)

    }

    private lateinit var onResultListener: OnResultListener
    private lateinit var binding: SelectImageBottomSheetDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectImageBottomSheetDialogFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectFromImageGallery.setOnClickListener { selectFromGallery() }
        binding.captureImageWithCamera.setOnClickListener { captureImageFromCamera() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle)
    }

    fun setOnResultListener(onResultListener: OnResultListener){
        this.onResultListener = onResultListener
    }

    fun selectFromGallery(){
        this.onResultListener.onResult(RESULT_GALLERY)
    }

     fun captureImageFromCamera(){
        this.onResultListener.onResult(RESULT_CAMERA)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}