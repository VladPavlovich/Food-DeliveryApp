package com.text.finalproject

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class ImageSelectionDialogFragment : DialogFragment() {

    interface ImageSelectionListener {
        fun onGallerySelected()
        fun onCameraSelected()
    }

    var listener: ImageSelectionListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(arrayOf("Select from Gallery", "Take Photo")) { _, which ->
                when (which) {
                    0 -> listener?.onGallerySelected()
                    1 -> listener?.onCameraSelected()
                }
            }
            .create()
    }
}
