package com.text.finalproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.text.finalproject.databinding.FragmentLogInScreenBinding

class LogInScreen : Fragment(), ImageSelectionDialogFragment.ImageSelectionListener {

    private var _binding: FragmentLogInScreenBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Request codes for gallery
    private val GALLERY_REQUEST_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.loginButton.setOnClickListener {
            var email = binding.emailEditText.text.toString()
            sharedViewModel.saveEmail(email)
            Log.i("Email", email)
            var password = binding.passwordEditText.text.toString()
            sharedViewModel.register(email, password)
            findNavController().navigate(R.id.action_logInScreen_to_homeScreen)
        }

        binding.profilePicture.setOnClickListener {
            showImageSelectionDialog()
        }

        return view
    }
//image selection dialog
    private fun showImageSelectionDialog() {
        val dialog = ImageSelectionDialogFragment()
        dialog.listener = this
        dialog.show(parentFragmentManager, "ImageSelectionDialog")
    }
//image selection dialog
    override fun onGallerySelected() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
//image selection dialog
    override fun onCameraSelected() {
        findNavController().navigate(R.id.action_logInScreen_to_takeImageFragment)
    }
//image selection dialog
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    binding.profilePicture.setImageURI(selectedImageUri)
                    sharedViewModel.setImageUri(selectedImageUri)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.profilePicture.setImageURI(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
