package ru.netology.nmedia.dto

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.netology.nmedia.R

fun ImageView.load(url: String, radius: Int = 100) {

    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_image_loading)
        .error(R.drawable.ic_load_error)
        .timeout(10_000)
        .transform(RoundedCorners(radius)) //round corners
        .into(this)
}