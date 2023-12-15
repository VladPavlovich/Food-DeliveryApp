package com.text.finalproject
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.text.finalproject.databinding.FragmentRestaurantImagesBinding
import kotlin.math.abs

class RestaurantImagesFragment : Fragment() {

    private var _binding: FragmentRestaurantImagesBinding? = null
    private val binding get() = _binding!!

    private var imageUrls: ArrayList<String> = arrayListOf()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrls = it.getStringArrayList("imageUrls") ?: arrayListOf()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRestaurantImagesBinding.inflate(inflater, container, false)
        setupImageViewSwipe()
        loadImage(currentIndex)
        return binding.root
    }
//swipe left and right to see the images
    private fun setupImageViewSwipe() {
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
//swipe left and right to see the images fling motion
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffY = e2!!.y - e1!!.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        return true
                    }
                }
                return false
            }
        })

        binding.imageView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }
//swipe left and right to see the images
    private fun onSwipeLeft() {
        if (currentIndex < imageUrls.size - 1) {
            currentIndex++
            loadImage(currentIndex)
        }
    }

    private fun onSwipeRight() {
        if (currentIndex > 0) {
            currentIndex--
            loadImage(currentIndex)
        }
    }
//loads the images
    private fun loadImage(index: Int) {
        // Check if the imageUrls list is not empty and index is valid
        if (imageUrls.isNotEmpty() && index in imageUrls.indices) {
            Glide.with(this)
                .load(imageUrls[index])
                .into(binding.imageView)
            Log.i("RestaurantImagesFragment", "Loaded image at index $index")
        } else {
            Log.i("RestaurantImagesFragment", "No image found at index $index")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
