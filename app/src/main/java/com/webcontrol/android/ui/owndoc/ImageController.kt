package com.webcontrol.android.ui.owndoc

import android.content.Intent
import android.os.Build
import com.webcontrol.android.ui.preacceso.CabeceraActivity

object ImageController {
    fun selectFileFromGallery(activity: UpdateDocFragment, code: Int) {
        val mimeTypes = arrayListOf("image/*","application/pdf")
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type="*/*"
                putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
            }
            activity.startActivityForResult(intent,code)
        } else  {
            var tmpMimes = ""
            mimeTypes.forEach {
                tmpMimes = "$it|$tmpMimes"
            }
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = tmpMimes.substring(0,tmpMimes.length-1)
            }
            activity.startActivityForResult(intent,code)
        }
    }
    fun selectFileFromGallery2(activity: CabeceraActivity, code: Int) {
        val mimeTypes = arrayListOf("image/*","application/pdf")
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type="*/*"
                putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
            }
            activity.startActivityForResult(intent,code)
        } else  {
            var tmpMimes = ""
            mimeTypes.forEach {
                tmpMimes = "$it|$tmpMimes"
            }
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = tmpMimes.substring(0,tmpMimes.length-1)
            }
            activity.startActivityForResult(intent,code)
        }
    }
}

