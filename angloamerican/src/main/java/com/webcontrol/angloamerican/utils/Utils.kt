package com.webcontrol.angloamerican.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.webcontrol.angloamerican.R
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @JvmStatic
    fun getNiceDate(inputDate: String?): String {
        val inputFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        var result = ""
        try {
            val date = inputFormat.parse(inputDate)
            result = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    @JvmStatic
    fun getFormatCredentialDate(inputDate: String?): String {
        val inputFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        var result = ""
        try {
            val date = inputDate?.let { inputFormat.parse(it) }
            result = date?.let { outputFormat.format(it) }.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun showNoRecordsDialog(context: Context) {
        val dialogBinding = LayoutInflater.from(context).inflate(R.layout.popup_information, null)
        val myDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        val title = dialogBinding.findViewById<TextView>(R.id.title)
        val first = dialogBinding.findViewById<TextView>(R.id.first)
        val second = dialogBinding.findViewById<TextView>(R.id.second)
        val third = dialogBinding.findViewById<TextView>(R.id.textInputLayoutPlate)
        title.text = context.getString(R.string.popup_add_comment_title)
        first.text = context.getString(R.string.error_in_vehicle_query)
        second.visibility = View.GONE
        third.visibility = View.GONE
        with(myDialog) {
            setContentView(dialogBinding)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            yesBtn.setOnClickListener { dismiss() }
        }
    }

    fun infoChecklist(context : Context){
        val dialogBinding = LayoutInflater.from(context).inflate(R.layout.popup_checklist_pre_uso, null)
        val myDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        with(myDialog) {
            setContentView(dialogBinding)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            yesBtn.setOnClickListener { dismiss() }
        }
    }
}