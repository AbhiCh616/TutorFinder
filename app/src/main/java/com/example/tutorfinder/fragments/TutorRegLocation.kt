package com.example.tutorfinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import me.tankery.lib.circularseekbar.CircularSeekBar
import java.text.DecimalFormat

class TutorRegLocation : Fragment() {

    // Views
    private lateinit var slider: CircularSeekBar
    private lateinit var distanceAnimationImage: ShapeableImageView
    private lateinit var distanceText: MaterialTextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutor_reg_location_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views initiated
        slider = requireView().findViewById(R.id.distance_slider)
        distanceAnimationImage = requireView().findViewById(R.id.distance_animation)
        distanceText = requireView().findViewById(R.id.distance_text)

        // Set on change listener
        slider.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                    circularSeekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
            ) {
                // Change the "street" image according to the slider
                distanceAnimationImage.setImageResource(
                        when (progress.toInt()) {
                            0 -> R.drawable.nothing_box
                            in 1..10 -> R.drawable.distance_animation_1
                            in 11..20 -> R.drawable.distance_animation_2
                            in 21..30 -> R.drawable.distance_animation_3
                            in 31..40 -> R.drawable.distance_animation_4
                            in 41..50 -> R.drawable.distance_animation_5
                            in 51..60 -> R.drawable.distance_animation_6
                            in 61..70 -> R.drawable.distance_animation_7
                            in 71..80 -> R.drawable.distance_animation_8
                            in 81..90 -> R.drawable.distance_animation_9
                            else -> R.drawable.distance_animation_10
                        }
                )

                // Display the number of kms the slider is set to
                distanceText.text = (String.format("%.1f", (progress / 10))
                        + " " + getString(R.string.km))
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {

            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {

            }
        }
        )
    }

}