package com.waddle.gentoo.internal.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.drawable.MovieDrawable
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal val String.urlEncoded: String
    get() {
        return try {
            URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            this
        }
    }

internal fun Int.toDp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

internal fun ImageView.loadGif(url: String, onGifAnimationEnded: () -> Unit = {}) {
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()
    val request = ImageRequest.Builder(context)
        .data(url)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .target { drawable ->
            when {
                drawable is MovieDrawable -> {
                    drawable.setRepeatCount(0)
                    drawable.registerAnimationCallback(
                        object : Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                onGifAnimationEnded()
                            }
                        }
                    )
                    this.setImageDrawable(drawable)
                    drawable.start()
                }

                else -> {
                    this.setImageDrawable(drawable)
                }
            }
        }
        .build()

    imageLoader.enqueue(request)
}
