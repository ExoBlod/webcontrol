package com.webcontrol.android.ui.common.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.webcontrol.android.R

class MessageView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    var imageView: ImageView
    var textView: TextView
    fun setDrawable(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    fun setDrawable(drawable: Int) {
        val img = ContextCompat.getDrawable(context, drawable)
        imageView.setImageDrawable(img)
    }

    fun setText(message: String?) {
        textView.text = message
    }

    fun setText(message: Int) {
        textView.setText(message)
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MessageView, 0, 0)
        val img = typedArray.getDrawable(R.styleable.MessageView_imageDrawable)
        val text = typedArray.getString(R.styleable.MessageView_messageText)
        typedArray.recycle()
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.message_view, this)
        imageView = findViewById(R.id.message_img)
        if (img != null) {
            imageView.setImageDrawable(img)
        }
        textView = findViewById(R.id.message_text)
        if (text != null) {
            textView.text = text
        }
    }
}